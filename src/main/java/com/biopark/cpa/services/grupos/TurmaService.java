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
import com.biopark.cpa.dto.grupos.TurmaDTO;
import com.biopark.cpa.entities.grupos.Curso;
import com.biopark.cpa.entities.grupos.Desafio;
import com.biopark.cpa.entities.grupos.Turma;
import com.biopark.cpa.form.grupos.TurmaModel;
import com.biopark.cpa.repository.grupo.TurmaRepository;
import com.biopark.cpa.services.utils.CsvParserService;
import com.biopark.cpa.services.utils.ValidaEntities;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TurmaService {
    private final CsvParserService csvParserService;
    private final CursoService cursoService;
    private final TurmaRepository turmaRepository;
    private final ValidaEntities validaEntities;

    @Transactional
    public CadastroDTO cadastrarTurma(List<Turma> turmas, boolean update) {
        List<ErroValidation> errors = csvParserService.validaEntrada(turmas);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        ValidationModel<Turma> model = verificaCurso(turmas);
        List<ErroValidation> naoExiste = model.getErrors();
        turmas = model.getObjects();

        if (!naoExiste.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.NOT_FOUND).erros(naoExiste).warnings(warnings).build();
        }

        if (!update) {
            model = checarDuplicatas(turmas);
            List<ErroValidation> duplicatas = model.getErrors();
            warnings = model.getWarnings();

            turmas = model.getObjects();

            if (!duplicatas.isEmpty()) {
                return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(duplicatas).warnings(warnings).build();
            }

            turmaRepository.saveAll(turmas);
            return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
        }

        for (Turma turma : turmas) {
            turmaRepository.upsert(turma);
        }

        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<Turma> checarDuplicatas(List<Turma> turmas) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<Turma> unicos = new ArrayList<>();

        Map<String, Integer> uniqueCod = new HashMap<String, Integer>();

        int linha = 0;
        for (Turma turma : turmas) {
            linha++;
            if (!uniqueCod.containsKey(turma.getCodTurma())) {
                uniqueCod.put(turma.getCodTurma(), linha);
                unicos.add(turma);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o código já existe na linha: "
                                + uniqueCod.get(turma.getCodTurma()))
                        .build());
                continue;
            }

            try {
                buscarPorCodigo(turma.getCodTurma());
                erroValidations
                        .add(ErroValidation.builder().linha(linha).mensagem("Turma já cadastrada").build());
            } catch (NoSuchElementException e) {
            }
        }

        return ValidationModel.<Turma>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    private ValidationModel<Turma> verificaCurso(List<Turma> turmas) {
        List<ErroValidation> erros = new ArrayList<>();

        int linha = 0;
        for (Turma turma : turmas) {
            linha++;

            try {
                Curso curso = cursoService.buscarPorCodigo(turma.getCodCurso());
                turma.setCurso(curso);
            } catch (NoSuchElementException e) {
                erros.add(
                        ErroValidation.builder()
                                .linha(linha)
                                .mensagem("O curso ligado a esta turma não está cadastrado")
                                .build());
            }
        }
        return ValidationModel.<Turma>builder().errors(erros).objects(turmas).build();
    }

    public Turma buscarPorCodigo(String codigo) {
        var optionalTurma = turmaRepository.findByCodTurma(codigo.toLowerCase());
        if (!optionalTurma.isPresent()) {
            throw new NoSuchElementException("Turma não encontrada!");
        }
        return optionalTurma.get();
    }

    public List<TurmaDTO> buscarTodasTurmasDTO() {
        List<Turma> turmas = turmaRepository.findAll();
        if (turmas.isEmpty()) {
            throw new NoSuchElementException("Não há turmas cadastradas!");
        }

        List<TurmaDTO> response = new ArrayList<>();

        for (Turma turma : turmas) {
            response.add(montaTurmaDTO(turma));
        }

        return response;
    }

    public Turma buscaPorId(Long id){
        var db = turmaRepository.findById(id);
        if (!db.isPresent()) {
            throw new NoSuchElementException("Turma não encontrada");
        }

        return db.get();
    }

    public TurmaDTO buscarPorCodigoDTO(String codigo){
        var optionalTurma = turmaRepository.findByCodTurma(codigo.toLowerCase());
        if (!optionalTurma.isPresent()) {
            throw new NoSuchElementException("Turma não encontrada!");
        }
        return montaTurmaDTO(optionalTurma.get());
    }

    public List<TurmaDTO> buscarPorCodigoCursoDTO(String codigo){
        List<Turma> turmas = turmaRepository.findAllByCursoCodCurso(codigo);
        if (turmas.isEmpty()) {
            throw new NoSuchElementException("Não há turmas cadastradas!");
        }

        List<TurmaDTO> response = new ArrayList<>();

        for (Turma turma : turmas) {
            response.add(montaTurmaDTO(turma));
        }

        return response;
    }

    private List<Turma> checaUniqueKeys(Turma turma){
        return turmaRepository.findUniqueKeys(turma);
    }

    private TurmaDTO montaTurmaDTO(Turma turma) {
        List<DesafioDTO> desafios = new ArrayList<>();
        for (Desafio desafio : turma.getDesafios()) {
            desafios.add(DesafioDTO.builder().id(desafio.getId()).nomeDesafio(desafio.getNomeDesafio()).build());
        }

        return TurmaDTO.builder()
                .id(turma.getId())
                .codTurma(turma.getCodTurma())
                .nomeTurma(turma.getNomeTurma())
                .semestre(turma.getSemestre())
                .codCurso(turma.getCurso().getCodCurso())
                .desafios(desafios)
                .build();
    }

    public GenericDTO editarTurma(TurmaModel turmaRequest){
        validaEntities.validaEntrada(turmaRequest);
        var db =  turmaRepository.findById(turmaRequest.getId());
        if (!db.isPresent()) {
            throw new NoSuchElementException("turma não encontrada");            
        }

        Turma turma = db.get();

        Curso curso = cursoService.buscarPorCodigo(turmaRequest.getCodCurso());
        
        boolean flag = ((turma.getCodTurma().equalsIgnoreCase(turmaRequest.getCodTurma()))|(turma.getNomeTurma().equalsIgnoreCase(turmaRequest.getNomeTurma()))) ? true : false;

        turma.setCodTurma(turmaRequest.getCodTurma());
        turma.setNomeTurma(turmaRequest.getNomeTurma());
        turma.setCurso(curso);
        turma.setSemestre(turmaRequest.getSemestre());
        turma.setCodCurso(turmaRequest.getCodCurso());

        List<Turma> uniqueKeys = checaUniqueKeys(turma);

        if ((flag && uniqueKeys.size() > 1)||((!flag) && (!uniqueKeys.isEmpty()))) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("nome ou codigo de turma já estão cadastrados").build();
        }

        turmaRepository.save(turma);

        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Turma editada com sucesso").build();
    }

    public GenericDTO excluirTurma(Long id) {
        if (!turmaRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("Turma não encontrada");
        }

        turmaRepository.deleteById(id);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Turma deletada com sucesso").build();
    }

}
