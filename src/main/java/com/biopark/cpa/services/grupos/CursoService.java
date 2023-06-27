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
import com.biopark.cpa.dto.grupos.CursoDTO;
import com.biopark.cpa.entities.grupos.Curso;
import com.biopark.cpa.entities.grupos.Instituicao;
import com.biopark.cpa.entities.grupos.Turma;
import com.biopark.cpa.entities.pessoas.Professor;
import com.biopark.cpa.form.grupos.CursoModel;
import com.biopark.cpa.repository.grupo.CursoRepository;
import com.biopark.cpa.repository.pessoas.ProfessorRepository;
import com.biopark.cpa.services.pessoas.ProfessorService;
import com.biopark.cpa.services.utils.CsvParserService;
import com.biopark.cpa.services.utils.ValidaEntities;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CursoService {
    private final CsvParserService csvParserService;
    private final InstituicaoService instituicaoService;
    private final ProfessorService professorService;
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;
    private final ValidaEntities validaEntities;

    @Transactional
    public CadastroDTO cadastrarCurso(List<Curso> cursos, boolean update) {
        List<ErroValidation> errors = csvParserService.validaEntrada(cursos);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        ValidationModel<Curso> model = verificaDependencias(cursos);
        List<ErroValidation> naoExiste = model.getErrors();
        cursos = model.getObjects();

        if (!naoExiste.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.NOT_FOUND).erros(naoExiste).warnings(warnings).build();
        }

        if (!update) {
            model = checarDuplicatas(cursos);
            List<ErroValidation> duplicatas = model.getErrors();
            warnings = model.getWarnings();
            cursos = model.getObjects();

            if (!duplicatas.isEmpty()) {
                return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(duplicatas).warnings(warnings).build();
            }

            cursoRepository.saveAll(cursos);
            return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
        }

        for (Curso curso : cursos) {
            cursoRepository.upsert(curso);
        }

        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<Curso> checarDuplicatas(List<Curso> cursos) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<Curso> unicosCod = new ArrayList<>();
        List<Curso> unicosNome = new ArrayList<>();

        Map<String, Integer> uniqueCod = new HashMap<String, Integer>();
        Map<String, Integer> uniqueNome = new HashMap<String, Integer>();

        int linha = 0;
        for (Curso curso : cursos) {
            curso.setCodCurso(curso.getCodCurso().toLowerCase());
            curso.setNomeCurso(curso.getNomeCurso().toLowerCase());

            linha++;
            if (!uniqueCod.containsKey(curso.getCodCurso())) {
                uniqueCod.put(curso.getCodCurso(), linha);
                unicosCod.add(curso);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o código já existe na linha: "
                                + uniqueCod.get(curso.getCodCurso()))
                        .build());
                continue;
            }

            if (!uniqueNome.containsKey(curso.getNomeCurso())) {
                uniqueNome.put(curso.getNomeCurso(), linha);
                unicosNome.add(curso);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o nome já existe na linha: "
                                + uniqueNome.get(curso.getNomeCurso()))
                        .build());
                continue;
            }
            if (!checaUniqueKey(curso).isEmpty()) {
                erroValidations.add(ErroValidation.builder().linha(linha).mensagem("Curso já cadastrado").build());
            }
        }

        List<Curso> unicos = unicosNome;
        unicos.retainAll(unicosCod);

        return ValidationModel.<Curso>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    private ValidationModel<Curso> verificaDependencias(List<Curso> cursos) {
        List<ErroValidation> erros = new ArrayList<>();

        int linha = 0;
        for (Curso curso : cursos) {
            linha++;

            try {
                Instituicao instituicao = instituicaoService.buscarPorCodigo(curso.getCodInstituicao());
                curso.setInstituicao(instituicao);
            } catch (NoSuchElementException e) {
                erros.add(
                        ErroValidation.builder()
                                .linha(linha)
                                .mensagem("A instituição ligada a este curso não está cadastrada")
                                .build());
            }

            try {
                Professor coordenador = professorService.buscarPorCracha(curso.getCrachaCoordenador());
                coordenador.setCoordenador(true);
                professorRepository.save(coordenador);
                curso.setProfessor(coordenador);
            } catch (Exception e) {
                erros.add(
                        ErroValidation.builder()
                                .linha(linha)
                                .mensagem("O coordenador ligado a este curso não está cadastrado")
                                .build());
            }

        }
        return ValidationModel.<Curso>builder().errors(erros).objects(cursos).build();
    }

    public CursoDTO buscarPorCodigoDTO(String codigo) {
        var optionalCurso = cursoRepository.findByCodCurso(codigo.toLowerCase());
        if (!optionalCurso.isPresent()) {
            throw new NoSuchElementException("Curso não encontrado!");
        }

        CursoDTO cursoDTO = montaCursoDTO(optionalCurso.get());

        return cursoDTO;
    }

    public Curso buscarPorCodigo(String codigo) {
        var optionalCurso = cursoRepository.findByCodCurso(codigo.toLowerCase());
        if (!optionalCurso.isPresent()) {
            throw new NoSuchElementException("Curso não encontrado");
        }

        return optionalCurso.get();
    }

    public Curso buscarCursoNome(String nome) {
        var optionalCurso = cursoRepository.findByNomeCurso(nome.toLowerCase());
        if (!optionalCurso.isPresent()) {
            throw new NoSuchElementException("Curso não encontrado!");
        }
        return optionalCurso.get();
    }

    public List<CursoDTO> buscarTodosCursosDTO() {
        List<Curso> cursos = cursoRepository.findAll();
        if (cursos.isEmpty()) {
            throw new NoSuchElementException("Não há cursos cadastrados!");
        }

        List<CursoDTO> cursosDTO = new ArrayList<>();

        for (Curso curso : cursos) {
            cursosDTO.add(montaCursoDTO(curso));
        }

        return cursosDTO;
    }

    public List<CursoDTO> buscarPorInstituicao(String codInstituicao) {
        List<Curso> cursos = cursoRepository.findAllByInstituicaoCodigoInstituicao(codInstituicao.toLowerCase());
        if (cursos.isEmpty()) {
            throw new NoSuchElementException("Não há cursos cadastrados!");
        }

        List<CursoDTO> response = new ArrayList<>();

        for (Curso curso : cursos) {
            response.add(montaCursoDTO(curso));
        }

        return response;
    }

    private List<Curso> checaUniqueKey(Curso curso) {
        return cursoRepository.findUniqueKey(curso);
    }

    private CursoDTO montaCursoDTO(Curso curso) {
        List<String> cods = curso.getTurmas().stream().map(Turma::getCodTurma).collect(Collectors.toList());

        return CursoDTO.builder()
                .id(curso.getId())
                .nomeCurso(curso.getNomeCurso())
                .codCurso(curso.getCodCurso())
                .instituicaoId(curso.getInstituicao().getId())
                .nomeInstituicao(curso.getInstituicao().getNomeInstituicao())
                .codInstituicao(curso.getInstituicao().getCodigoInstituicao())
                .turmasCod(cods)
                .coordenadorId(curso.getProfessor().getId())
                .nomeCoordenador(curso.getProfessor().getUser().getName())
                .crachaCoordenador(curso.getProfessor().getCracha())
                .build();
    }

    @Transactional
    public GenericDTO editarCurso(CursoModel cursoRequest) {
        validaEntities.validaEntrada(cursoRequest);
        var db =  cursoRepository.findById(cursoRequest.getId());
        if (!db.isPresent()) {
            throw new NoSuchElementException("curso não encontrada");            
        }

        Curso curso = db.get();

        Instituicao instituicao = instituicaoService.buscarPorCodigo(cursoRequest.getCodInstituicao());
        Professor professor = professorService.buscarPorCracha(cursoRequest.getCrachaCoordenador());

        boolean flag = ((curso.getNomeCurso().equalsIgnoreCase(cursoRequest.getNomeCurso()))
            ||(curso.getCodCurso().equalsIgnoreCase(cursoRequest.getCodCurso()))) ? true : false;

        curso.setNomeCurso(cursoRequest.getNomeCurso());
        curso.setCodCurso(cursoRequest.getCodCurso());
        curso.setCrachaCoordenador(cursoRequest.getCrachaCoordenador());
        curso.setCodInstituicao(cursoRequest.getCodInstituicao());
        curso.setProfessor(professor);
        curso.setInstituicao(instituicao);

        List<Curso> uniqueKeys = checaUniqueKey(curso);

        if (((flag) && (uniqueKeys.size()>1))||((!flag) && (uniqueKeys.size()>0))) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Nome ou código do curso já estão cadastrados").build();
        }

        cursoRepository.save(curso);
        professor.setCoordenador(true);
        professorRepository.save(professor);

        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Curso editado com sucesso").build();
    }

    public GenericDTO excluirCurso(Long id) {
        if (!cursoRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("Curso não encontrado");
        }

        cursoRepository.deleteById(id);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Curso deletado com sucesso").build();
    }
}
