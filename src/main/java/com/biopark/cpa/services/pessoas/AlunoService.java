package com.biopark.cpa.services.pessoas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.cadastroCsv.ErroValidation;
import com.biopark.cpa.dto.cadastroCsv.ValidationModel;
import com.biopark.cpa.entities.grupos.DesafioTurma;
import com.biopark.cpa.entities.grupos.Turma;
import com.biopark.cpa.entities.pessoas.Aluno;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.entities.user.enums.Level;
import com.biopark.cpa.entities.user.enums.Role;
import com.biopark.cpa.form.cadastroCsv.AlunoModel;
import com.biopark.cpa.repository.grupo.DesafioTurmaRepository;
import com.biopark.cpa.repository.pessoas.AlunoRepository;
import com.biopark.cpa.repository.pessoas.UserRepository;
import com.biopark.cpa.services.security.GeneratePassword;
import com.biopark.cpa.services.utils.CsvParserService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AlunoService {

    private final CsvParserService csvService;
    private final UserService userService;
    private final DesafioTurmaRepository desafioTurmaRepository;
    private final UserRepository userRepository;
    private final AlunoRepository alunoRepository;
    private final GeneratePassword generatePassword;

    @Transactional
    public CadastroDTO cadastrarAluno(List<AlunoModel> alunosModel, boolean update) {
        List<ErroValidation> errors = csvService.validaEntrada(alunosModel);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        ValidationModel<AlunoModel> model = verificaDependencias(alunosModel);

        if (!model.getErrors().isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.NOT_FOUND).erros(model.getErrors()).warnings(warnings)
                    .build();
        }

        alunosModel = model.getObjects();

        if (!update) {
            ValidationModel<AlunoModel> modelDuplicate = checarDuplicatas(alunosModel);

            if (!modelDuplicate.getErrors().isEmpty()) {
                return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(modelDuplicate.getErrors())
                        .warnings(modelDuplicate.getWarnings()).build();
            }

            List<User> users = new ArrayList<>();
            List<Aluno> alunos = new ArrayList<>();

            for (AlunoModel alunoModel : alunosModel) {
                User user = User.builder()
                        .cpf(alunoModel.getCpf())
                        .name(alunoModel.getName())
                        .telefone(alunoModel.getTelefone())
                        .email(alunoModel.getEmail())
                        .password(generatePassword.getPwd())
                        .role(Role.ALUNO)
                        .level(Level.USER)
                        .build();

                alunos.add(Aluno.builder().ra(alunoModel.getRa()).desafioTurmas(alunoModel.getDesafiosTurma()).user(user).build());
                users.add(user);
            }

            userRepository.saveAll(users);
            alunoRepository.saveAll(alunos);
            return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
        }

        for (AlunoModel alunoModel : alunosModel) {
            User user = User.builder()
                    .cpf(alunoModel.getCpf())
                    .email(alunoModel.getEmail())
                    .name(alunoModel.getName())
                    .telefone(alunoModel.getTelefone())
                    .password(generatePassword.getPwd())
                    .role(Role.ALUNO)
                    .level(Level.USER)
                    .build();

            user = userService.buscarPorCpf(user.getCpf());

            Aluno aluno = Aluno.builder().ra(alunoModel.getRa()).desafioTurmas(alunoModel.getDesafiosTurma()).user(user)
                    .build();

            userRepository.upsert(user);
            alunoRepository.upsert(aluno);

            aluno = buscarPorRa(alunoModel.getRa());
            aluno.setDesafioTurmas(alunoModel.getDesafiosTurma());
            alunoRepository.save(aluno);
        }

        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<AlunoModel> verificaDependencias(List<AlunoModel> alunos) {
        List<ErroValidation> erros = new ArrayList<>();

        int linha = 0;

        for (AlunoModel aluno : alunos) {
            linha++;

            List<DesafioTurma> responseDB = desafioTurmaRepository
                    .findAllByTurmaCodTurma(aluno.getCodTurma().toLowerCase()).get();

            if (responseDB.isEmpty()) {
                erros.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Não temos nenhum desafio cadastrado a turma informada")
                        .build());
            }

            aluno.setDesafiosTurma(responseDB);
        }

        return ValidationModel.<AlunoModel>builder().errors(erros).objects(alunos).build();
    }

    private ValidationModel<AlunoModel> checarDuplicatas(List<AlunoModel> models){
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<AlunoModel> unicosEmail = new ArrayList<>();
        List<AlunoModel> unicosRa = new ArrayList<>();
        List<AlunoModel> unicosCpf = new ArrayList<>();

        HashMap<String, Integer> uniqueEmail = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueRa = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueCpf = new HashMap<String, Integer>();

        int linha = 0;
        for (AlunoModel aluno : models) {
            linha++;

            if (!uniqueEmail.containsKey(aluno.getEmail())) {
                uniqueEmail.put(aluno.getEmail(), linha);
                unicosEmail.add(aluno);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o Email já existe na linha: "
                                + uniqueEmail.get(aluno.getEmail()))
                        .build());
                continue;
            }

            if (!uniqueRa.containsKey(aluno.getRa())) {
                uniqueRa.put(aluno.getRa(), linha);
                unicosRa.add(aluno);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o RA já existe na linha: "
                                + uniqueRa.get(aluno.getRa()))
                        .build());
                continue;
            }

            if (!uniqueCpf.containsKey(aluno.getCpf())) {
                uniqueCpf.put(aluno.getCpf(), linha);
                unicosCpf.add(aluno);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o cpf já existe na linha: "
                                + uniqueCpf.get(aluno.getCpf()))
                        .build());
                continue;
            }

            try {
                userService.buscarPorCpf(aluno.getCpf());
                userService.buscarPorEmail(aluno.getEmail());
                buscarPorRa(aluno.getRa());
                erroValidations.add(ErroValidation.builder().linha(linha).mensagem("Aluno já cadastrado").build());   
            } catch (Exception e) {
            }
        }

        List<AlunoModel> unicos = unicosEmail;
        unicos.retainAll(unicosRa);

        return ValidationModel.<AlunoModel>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    public List<Aluno> buscarPorTurma(Long id){
        return alunoRepository.findByDesafioTurmas_turma_id(id);
    }

    public List<Aluno> listarAlunosTurma(Turma turma){
        List<Aluno> alunos = alunoRepository.findByDesafioTurmas_turma_id(turma.getId());
        return alunos;
    }

    public Aluno buscarPorRa(String ra){
        var optional = alunoRepository.findByra(ra.toLowerCase());

        if (!optional.isPresent()) {
            throw new NoSuchElementException();
        }

        return optional.get();
    }
}
