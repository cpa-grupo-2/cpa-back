package com.biopark.cpa.services.grupos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.cadastroCsv.ErroValidation;
import com.biopark.cpa.dto.cadastroCsv.ValidationModel;
import com.biopark.cpa.dto.grupos.DesafioDTO;
import com.biopark.cpa.entities.grupos.Desafio;
import com.biopark.cpa.entities.grupos.DesafioTurma;
import com.biopark.cpa.entities.grupos.Turma;
import com.biopark.cpa.entities.pessoas.Aluno;
import com.biopark.cpa.form.grupos.DesafioModel;
import com.biopark.cpa.form.grupos.DesafioTurmaModel;
import com.biopark.cpa.repository.grupo.DesafioRepository;
import com.biopark.cpa.repository.grupo.DesafioTurmaRepository;
import com.biopark.cpa.services.pessoas.AlunoService;
import com.biopark.cpa.services.utils.CsvParserService;
import com.biopark.cpa.services.utils.ValidaEntities;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DesafioService {
    private final CsvParserService csvParserService;
    private final AlunoService alunoService;
    private final TurmaService turmaService;
    private final DesafioRepository desafioRepository;
    private final DesafioTurmaRepository desafioTurmaRepository;
    private final ValidaEntities validaEntities;

    @Transactional
    public CadastroDTO cadastrarDesafio(List<Desafio> desafios) {
        List<ErroValidation> errors = csvParserService.validaEntrada(desafios);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        ValidationModel<Desafio> model = checarDuplicatas(desafios);
        List<ErroValidation> duplicatas = model.getErrors();
        warnings = model.getWarnings();

        desafios = model.getObjects();

        if (!duplicatas.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(duplicatas).warnings(warnings).build();
        }

        desafioRepository.saveAll(desafios);
        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<Desafio> checarDuplicatas(List<Desafio> desafios) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<Desafio> unicos = new ArrayList<>();

        Map<String, Integer> uniqueCod = new HashMap<String, Integer>();

        int linha = 0;
        for (Desafio desafio : desafios) {
            linha++;
            if (!uniqueCod.containsKey(desafio.getNomeDesafio())) {
                uniqueCod.put(desafio.getNomeDesafio(), linha);
                unicos.add(desafio);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o código já existe na linha: "
                                + uniqueCod.get(desafio.getNomeDesafio()))
                        .build());
                continue;
            }

            try {
                buscarPorNome(desafio.getNomeDesafio());
                erroValidations
                        .add(ErroValidation.builder().linha(linha).mensagem("Desafio já cadastrado").build());
            } catch (NoSuchElementException e) {
            }
        }
        return ValidationModel.<Desafio>builder().errors(erroValidations).warnings(warnings).objects(unicos).build();
    }

    public GenericDTO associarDesafioTurma(DesafioTurmaModel desafioTurmaModel) {
        validaEntities.validaEntrada(desafioTurmaModel);
        desafioTurmaModel.getDesafioId().removeIf(item -> item == null);

        if (desafioTurmaModel.getDesafioId().size() < 1) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("não foi informado nenhum desafio").build();
        }

        if (desafioTurmaModel.getTurmaId() == 0) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("não foi informado nenhuma turma").build();
        }

        Turma turma = turmaService.buscaPorId(desafioTurmaModel.getTurmaId());

        List<Desafio> desafios = new ArrayList<>();

        for (Long id : desafioTurmaModel.getDesafioId()) {
            Desafio desafio = buscaPorId(id);
            desafios.add(desafio);
        }

        desafios.removeAll(turma.getDesafios());

        if (desafios.isEmpty()) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT)
                .mensagem("As disciplinas já tem relação com esta turma").build();
        }

        List<DesafioTurma> relacao = new ArrayList<>();
        List<Aluno> alunos = alunoService.buscarPorTurma(turma.getId());

        for (Desafio desafio : desafios) {
            relacao.add(DesafioTurma.builder().turma(turma).desafio(desafio).alunos(alunos).build());
        }

        desafioTurmaRepository.saveAll(relacao);

        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Disciplinas associadas com sucesso").build();
    }

    public List<DesafioDTO> buscarTodosDTO() {
        List<Desafio> desafios = desafioRepository.findAll();
        if (desafios.isEmpty()) {
            throw new NoSuchElementException("Não há desafios cadastrados!");
        }

        List<DesafioDTO> response = new ArrayList<>();

        for (Desafio desafio : desafios) {
            response.add(montaDesafioDTO(desafio));
        }

        return response;
    }

    public Desafio buscaPorId(Long id){
        var db = desafioRepository.findById(id);
        if (!db.isPresent()) {
            throw new NoSuchElementException("desafio"+id+" não encontrado");
        }

        return db.get();
    }

    public Desafio buscarPorNome(String nome) {
        var optional = desafioRepository.findByNomeDesafio(nome.toLowerCase());
        if (!optional.isPresent()) {
            throw new NoSuchElementException("Não há desafios cadastrados!");
        }
        return optional.get();
    }

    public List<DesafioDTO> buscaNomeParcial(String termo) {
        List<Desafio> desafios = desafioRepository.findByNomeDesafioLike("%" + termo.toLowerCase() + "%");
        if (desafios.isEmpty()) {
            throw new NoSuchElementException("Não há desafios cadastrados!");
        }

        List<DesafioDTO> response = new ArrayList<>();

        for (Desafio desafio : desafios) {
            response.add(montaDesafioDTO(desafio));
        }

        return response;
    }

    private DesafioDTO montaDesafioDTO(Desafio desafio) {
        return DesafioDTO.builder().id(desafio.getId()).nomeDesafio(desafio.getNomeDesafio()).build();
    }

    public GenericDTO editarDesafio(DesafioModel desafioRequest) {
        validaEntities.validaEntrada(desafioRequest);
        var db = desafioRepository.findById(desafioRequest.getId());
        if (!db.isPresent()) {
            throw new NoSuchElementException("desafio não encontrado");
        }

        Desafio desafio = db.get();

        if ((!desafio.getNomeDesafio().equalsIgnoreCase(desafioRequest.getNomeDesafio()))
                && (!desafioRepository.findByNomeDesafio(desafioRequest.getNomeDesafio().toLowerCase()).isEmpty())) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Nome do desafio já esta cadastrado")
                    .build();
        }

        desafio.setNomeDesafio(desafioRequest.getNomeDesafio());

        desafioRepository.save(desafio);

        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Desafio editado com sucesso").build();
    }

    public GenericDTO excluirDesafio(Long id) {
        var db = desafioRepository.findById(id);
        if (!db.isPresent()) {
            throw new NoSuchElementException("desafio não encontrado");
        }

        desafioRepository.deleteById(id);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Desafio deletado com sucesso").build();
    }
}
