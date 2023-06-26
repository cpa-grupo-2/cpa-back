package com.biopark.cpa.services.grupos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.cadastroCsv.ErroValidation;
import com.biopark.cpa.dto.cadastroCsv.ValidationModel;
import com.biopark.cpa.dto.grupos.InstituicaoDTO;
import com.biopark.cpa.entities.grupos.Curso;
import com.biopark.cpa.entities.grupos.Instituicao;
import com.biopark.cpa.form.grupos.InstituicaoModel;
import com.biopark.cpa.repository.grupo.InstituicaoRepository;
import com.biopark.cpa.services.utils.CsvParserService;
import com.biopark.cpa.services.utils.ValidaEntities;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InstituicaoService {
    private final CsvParserService csvParserService;
    private final InstituicaoRepository instituicaoRepository;
    private final ValidaEntities validaEntities;

    @Transactional
    public CadastroDTO cadastrarInstituicao(List<Instituicao> instituicoes, boolean update) {
        List<ErroValidation> errors = csvParserService.validaEntrada(instituicoes);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        if (!update) {
            ValidationModel<Instituicao> model = checarDuplicatas(instituicoes);
            List<ErroValidation> duplicatas = model.getErrors();
            warnings = model.getWarnings();

            instituicoes = model.getObjects();

            if (!duplicatas.isEmpty()) {
                return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(duplicatas).warnings(warnings).build();
            }

            instituicaoRepository.saveAll(instituicoes);
            return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
        }

        upsert(instituicoes);

        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<Instituicao> checarDuplicatas(List<Instituicao> instituicoes) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<Instituicao> unicos = new ArrayList<>();

        Map<String, Integer> uniqueCod = new HashMap<String, Integer>();

        int linha = 0;
        for (Instituicao instituicao : instituicoes) {
            instituicao.setCodigoInstituicao(instituicao.getCodigoInstituicao().toLowerCase());
            linha++;
            if (!uniqueCod.containsKey(instituicao.getCodigoInstituicao())) {
                uniqueCod.put(instituicao.getCodigoInstituicao(), linha);
                unicos.add(instituicao);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o código já existe na linha: "
                                + uniqueCod.get(instituicao.getCodigoInstituicao()))
                        .build());
                continue;
            }

            if (checaUniqueKey(instituicao).size()>0) {
                erroValidations
                        .add(ErroValidation.builder().linha(linha).mensagem("Instituição já cadastrada").build());
            }
        }

        return ValidationModel.<Instituicao>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    public void upsert(List<Instituicao> iter) {
        for (Instituicao instituicao : iter) {
            instituicaoRepository.upsert(instituicao);
        }
    }

    public InstituicaoDTO buscarPorCodigoDTO(String codigo) {
        var optionalInstituicao = instituicaoRepository.findByCodigoInstituicao(codigo.toLowerCase().strip());
        if (!optionalInstituicao.isPresent()) {
            throw new NoSuchElementException("Instituição não encontrada!");
        }

        Instituicao instituicao = optionalInstituicao.get();

        return montaResponse(instituicao);
    }

    public List<InstituicaoDTO> buscarTodasInstituicoesDTO() {
        List<Instituicao> instituicoes = instituicaoRepository.findAll();
        if (instituicoes.isEmpty()) {
            throw new NoSuchElementException("Não há instituições cadastradas!");
        }

        List<InstituicaoDTO> response = new ArrayList<>();

        for (Instituicao instituicao : instituicoes) {
            response.add(montaResponse(instituicao));
        }

        return response;
    }

    public Instituicao buscarPorCodigo(String codigo){
        var optionalInstituicao = instituicaoRepository.findByCodigoInstituicao(codigo.toLowerCase().strip());
        if (!optionalInstituicao.isPresent()) {
            throw new NoSuchElementException("Instituição não encontrada!");
        }

        Instituicao instituicao = optionalInstituicao.get();

        return instituicao;
    }

    private List<Instituicao> checaUniqueKey(Instituicao instituicao){
        return instituicaoRepository.findUniqueKey(instituicao);
    }

    private InstituicaoDTO montaResponse(Instituicao instituicao){
        List<String> cods = instituicao.getCursos().stream().map(Curso::getCodCurso).collect(Collectors.toList());

        InstituicaoDTO response = InstituicaoDTO.builder()
            .id(instituicao.getId())
            .codigoInstituicao(instituicao.getCodigoInstituicao())
            .nomeInstituicao(instituicao.getNomeInstituicao())
            .email(instituicao.getEmail())
            .cnpj(instituicao.getCnpj())
            .cursosCod(cods)
            .build();

        return response;
    }

    public GenericDTO editarInstituicao(InstituicaoModel instituicaoRequest) {
        validaEntities.validaEntrada(instituicaoRequest);
        var db =  instituicaoRepository.findById(instituicaoRequest.getId());
        if (!db.isPresent()) {
            throw new NoSuchElementException("instituicao não encontrada");            
        }

        Instituicao instituicao = db.get();

        boolean flag = (!instituicao.getCodigoInstituicao().equalsIgnoreCase(instituicaoRequest.getCodigoInstituicao())) ? true : false;

        instituicao.setNomeInstituicao(instituicaoRequest.getNomeInstituicao());
        instituicao.setCnpj(instituicaoRequest.getCnpj());
        instituicao.setEmail(instituicaoRequest.getEmail());
        instituicao.setCodigoInstituicao(instituicaoRequest.getCodigoInstituicao());

        if (flag) {
            if (checaUniqueKey(instituicao).size() > 0) {
                return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("O código de instituição já se encontra no banco de dados").build();
            }
        }
        instituicaoRepository.save(instituicao);

        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Instituição editada com sucesso").build();
    }

    public GenericDTO excluirInstituicao(Long id) {
        if (!instituicaoRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("Instituicao não encontrada");
        }

        instituicaoRepository.deleteById(id);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Instituição deletada com sucesso").build();
    }
}
