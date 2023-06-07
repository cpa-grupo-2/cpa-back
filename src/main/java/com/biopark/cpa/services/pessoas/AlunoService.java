package com.biopark.cpa.services.pessoas;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.cadastroCsv.ErroValidation;
import com.biopark.cpa.dto.cadastroCsv.ValidationModel;
import com.biopark.cpa.entities.grupos.DesafioTurma;
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

            userRepository.upsert(user);

            user = userRepository.findByCpf(user.getCpf()).get();

            Aluno aluno = Aluno.builder().ra(alunoModel.getRa()).desafioTurmas(alunoModel.getDesafiosTurma()).user(user).build();

            alunoRepository.upsert(aluno);            

            aluno = alunoRepository.findByra(alunoModel.getRa().toLowerCase()).get();
            // alunoRepository.deleteAlunoDesafioTurma(aluno);

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
}
