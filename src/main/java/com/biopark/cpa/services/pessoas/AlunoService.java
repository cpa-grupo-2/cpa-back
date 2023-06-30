package com.biopark.cpa.services.pessoas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.cadastroCsv.ErroValidation;
import com.biopark.cpa.dto.cadastroCsv.ValidationModel;
import com.biopark.cpa.dto.grupos.DesafioTurmaDTO;
import com.biopark.cpa.dto.pessoas.AlunoDTO;
import com.biopark.cpa.entities.grupos.DesafioTurma;
import com.biopark.cpa.entities.pessoas.Aluno;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.entities.user.enums.Level;
import com.biopark.cpa.entities.user.enums.Role;
import com.biopark.cpa.form.cadastroCsv.AlunoModelCsv;
import com.biopark.cpa.form.pessoas.AlunoModel;
import com.biopark.cpa.repository.grupo.DesafioTurmaRepository;
import com.biopark.cpa.repository.pessoas.AlunoRepository;
import com.biopark.cpa.repository.pessoas.UserRepository;
import com.biopark.cpa.services.security.GeneratePassword;
import com.biopark.cpa.services.utils.CsvParserService;
import com.biopark.cpa.services.utils.ValidaEntities;

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
    private final ValidaEntities validaEntities;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public CadastroDTO cadastrarAluno(List<AlunoModelCsv> alunosModel, boolean update) {
        List<ErroValidation> errors = csvService.validaEntrada(alunosModel);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        ValidationModel<AlunoModelCsv> model = verificaDependencias(alunosModel);

        if (!model.getErrors().isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.NOT_FOUND).erros(model.getErrors()).warnings(warnings)
                    .build();
        }

        alunosModel = model.getObjects();

        if (!update) {
            ValidationModel<AlunoModelCsv> modelDuplicate = checarDuplicatas(alunosModel);

            if (!modelDuplicate.getErrors().isEmpty()) {
                return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(modelDuplicate.getErrors())
                        .warnings(modelDuplicate.getWarnings()).build();
            }

            List<User> users = new ArrayList<>();
            List<Aluno> alunos = new ArrayList<>();

            for (AlunoModelCsv alunoModel : alunosModel) {
                User user = User.builder()
                        .cpf(alunoModel.getCpf())
                        .name(alunoModel.getName())
                        .telefone(alunoModel.getTelefone())
                        .email(alunoModel.getEmail())
                        .password(generatePassword.getPwd())
                        .role(Role.ALUNO)
                        .level(Level.USER)
                        .build();

                alunos.add(Aluno.builder().ra(alunoModel.getRa()).desafioTurmas(alunoModel.getDesafiosTurma())
                        .user(user).build());
                users.add(user);
            }

            userRepository.saveAll(users);
            alunoRepository.saveAll(alunos);
            return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
        }

        for (AlunoModelCsv alunoModel : alunosModel) {
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
            user = userService.buscarPorCpf(user.getCpf());

            Aluno aluno = Aluno.builder().ra(alunoModel.getRa()).desafioTurmas(alunoModel.getDesafiosTurma()).user(user)
                    .build();

            alunoRepository.upsert(aluno);

            aluno = buscarPorRa(alunoModel.getRa());
            aluno.setDesafioTurmas(alunoModel.getDesafiosTurma());
            alunoRepository.save(aluno);
        }

        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<AlunoModelCsv> verificaDependencias(List<AlunoModelCsv> alunos) {
        List<ErroValidation> erros = new ArrayList<>();

        int linha = 0;

        for (AlunoModelCsv aluno : alunos) {
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

        return ValidationModel.<AlunoModelCsv>builder().errors(erros).objects(alunos).build();
    }

    private ValidationModel<AlunoModelCsv> checarDuplicatas(List<AlunoModelCsv> models) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<AlunoModelCsv> unicosEmail = new ArrayList<>();
        List<AlunoModelCsv> unicosRa = new ArrayList<>();
        List<AlunoModelCsv> unicosCpf = new ArrayList<>();

        HashMap<String, Integer> uniqueEmail = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueRa = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueCpf = new HashMap<String, Integer>();

        int linha = 0;
        for (AlunoModelCsv aluno : models) {
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

            if ((!userService.checarUniqueKey(aluno.getCpf(), aluno.getEmail()).isEmpty())||(!checarUniqueKeys(aluno.getRa()).isEmpty())) {
                erroValidations.add(ErroValidation.builder().linha(linha).mensagem("Aluno já cadastrado").build());
            }
        }

        List<AlunoModelCsv> unicos = unicosEmail;
        unicos.retainAll(unicosRa);

        return ValidationModel.<AlunoModelCsv>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    public List<AlunoDTO> buscarTodosDTO(){
        List<Aluno> alunos = alunoRepository.findAll();
        if (alunos.isEmpty()) {
            throw new NoSuchElementException("Nenhum aluno encontrado");
        }

        List<AlunoDTO> response = new ArrayList<>();

        for (Aluno aluno : alunos) {
            response.add(montaAlunoDTO(aluno));
        }

        return response;
    }

    public AlunoDTO buscarPorRADTO(String ra){
        var db = alunoRepository.findByra(ra.toLowerCase());
        if (!db.isPresent()) {
            throw new NoSuchElementException("Aluno não encontrado");
        }

        return montaAlunoDTO(db.get());
    }

    public List<AlunoDTO> buscarPorTurmaDTO(String codTurma) {
        List<Aluno> alunos = alunoRepository.findAllByDesafioTurmas_turma_codTurma(codTurma);
        if (alunos.isEmpty()) {
            throw new NoSuchElementException("Alunos não encontrados");
        }

        List<AlunoDTO> response = new ArrayList<>();

        for (Aluno aluno : alunos) {
            response.add(montaAlunoDTO(aluno));
        }

        return response;
    }

    private AlunoDTO montaAlunoDTO(Aluno aluno){
        List<DesafioTurmaDTO> desafioTurmas = new ArrayList<>();

        for (DesafioTurma desafioTurma : aluno.getDesafioTurmas()) {
            desafioTurmas.add(
                DesafioTurmaDTO.builder().id(desafioTurma.getId())
                    .codTurma(desafioTurma.getTurma().getCodTurma())
                    .nomeDesafio(desafioTurma.getDesafio().getNomeDesafio())
                    .build()
            );
        }

        return AlunoDTO.builder()
            .id(aluno.getId())
            .cpf(aluno.getUser().getCpf())
            .name(aluno.getUser().getName())
            .telefone(aluno.getUser().getTelefone())
            .email(aluno.getUser().getEmail())
            .level(aluno.getUser().getLevel().name())
            .ra(aluno.getRa())
            .desafioTurma(desafioTurmas)
            .build();
    }
    
    public Aluno buscarPorRa(String ra) {
        var optional = alunoRepository.findByra(ra.toLowerCase());

        if (!optional.isPresent()) {
            throw new NoSuchElementException("Aluno não encontrado");
        }

        return optional.get();
    }

    public List<Aluno> buscarPorTurma(Long id){
        List<Aluno> alunos = alunoRepository.findByDesafioTurmas_turma_id(id);
        return alunos;
    }

    public Aluno buscarPorId(Long id){
        var db = alunoRepository.findById(id);
        if (!db.isPresent()) {
            throw new NoSuchElementException("Aluno Não encontrado");
        }

        return db.get();
    }

    private List<Aluno> checarUniqueKeys(String ra){
        List<Aluno> alunos = alunoRepository.findUniqueKeys(ra);
        return alunos;
    }

    private List<Aluno> checarUniqueKeys(Aluno aluno){
        List<Aluno> alunos = alunoRepository.findUniqueKeys(aluno);
        return alunos;
    }

    public GenericDTO editar(AlunoModel model){
        validaEntities.validaEntrada(model);
        Aluno aluno = buscarPorId(model.getId());

        boolean flag = (aluno.getUser().getEmail().equalsIgnoreCase(model.getEmail())) ? true : false;
        flag = ((aluno.getUser().getCpf().equalsIgnoreCase(model.getCpf())) || (flag)) ? true : false;
    
        boolean flagAluno = aluno.getRa().equalsIgnoreCase(model.getRa());

        aluno.setRa(model.getRa());
        aluno.getUser().setCpf(model.getCpf());
        aluno.getUser().setEmail(model.getEmail());
        aluno.getUser().setPassword(passwordEncoder.encode(model.getPassword()));
        aluno.getUser().setName(model.getName());
        aluno.getUser().setTelefone(model.getTelefone());

        if (((flag) && (userService.checaUniqueKey(aluno.getUser()).size() > 1)) ||
                ((!flag) && (userService.checaUniqueKey(aluno.getUser()).size() > 0))) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Aluno já existe").build();
        }

        if ((!flagAluno) && (!checarUniqueKeys(aluno).isEmpty())) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Aluno já existe").build();
        }

        alunoRepository.save(aluno);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Aluno editado com sucesso").build();
    }

    public GenericDTO deletar(Long id){
        Aluno aluno = buscarPorId(id);
        alunoRepository.delete(aluno);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Aluno Deletado com sucesso").build();
    }
}
