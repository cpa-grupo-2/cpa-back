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
import com.biopark.cpa.dto.pessoas.FuncionarioDTO;
import com.biopark.cpa.entities.pessoas.Funcionario;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.entities.user.enums.Level;
import com.biopark.cpa.entities.user.enums.Role;
import com.biopark.cpa.form.cadastroCsv.FuncionarioModelCsv;
import com.biopark.cpa.form.pessoas.FuncionarioModel;
import com.biopark.cpa.repository.pessoas.FuncionarioRepository;
import com.biopark.cpa.repository.pessoas.UserRepository;
import com.biopark.cpa.services.security.GeneratePassword;
import com.biopark.cpa.services.utils.CsvParserService;
import com.biopark.cpa.services.utils.ValidaEntities;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FuncionarioService {
    private final CsvParserService csvParserService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final GeneratePassword generatePassword;
    private final PasswordEncoder passwordEncoder;
    private final ValidaEntities validaEntities;

    @Transactional
    public CadastroDTO cadastrarFuncionario(List<FuncionarioModelCsv> funcionariosModel, boolean update) {
        List<ErroValidation> errors = csvParserService.validaEntrada(funcionariosModel);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        if (!update) {

            ValidationModel<FuncionarioModelCsv> model = checarDuplicatas(funcionariosModel);
            List<ErroValidation> duplicatas = model.getErrors();
            warnings = model.getWarnings();
            funcionariosModel = model.getObjects();

            if (!duplicatas.isEmpty()) {
                return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(duplicatas).warnings(warnings).build();
            }

            List<User> users = new ArrayList<>();
            List<Funcionario> funcionarios = new ArrayList<>();

            for (FuncionarioModelCsv funcionarioModel : funcionariosModel) {
                User user = User.builder()
                        .cpf(funcionarioModel.getCpf())
                        .name(funcionarioModel.getName())
                        .telefone(funcionarioModel.getTelefone())
                        .email(funcionarioModel.getEmail())
                        .password(generatePassword.getPwd())
                        .role(Role.FUNCIONARIO)
                        .level(Level.USER)
                        .build();

                Funcionario funcionario = Funcionario.builder()
                        .cracha(funcionarioModel.getCracha())
                        .area(funcionarioModel.getArea())
                        .user(user)
                        .build();

                users.add(user);
                funcionarios.add(funcionario);

            }

            userRepository.saveAll(users);
            funcionarioRepository.saveAll(funcionarios);

            return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
        }

        for (FuncionarioModelCsv funcionarioModel : funcionariosModel) {
            User user = User.builder()
                    .cpf(funcionarioModel.getCpf())
                    .name(funcionarioModel.getName())
                    .telefone(funcionarioModel.getTelefone())
                    .email(funcionarioModel.getEmail())
                    .password(generatePassword.getPwd())
                    .role(Role.FUNCIONARIO)
                    .level(Level.USER)
                    .build();

            userRepository.upsert(user);

            user = userService.buscarPorCpf(funcionarioModel.getCpf());

            Funcionario funcionario = Funcionario.builder()
                    .cracha(funcionarioModel.getCracha())
                    .area(funcionarioModel.getArea())
                    .user(user)
                    .build();

            funcionarioRepository.upsert(funcionario);
        }

        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<FuncionarioModelCsv> checarDuplicatas(List<FuncionarioModelCsv> funcionarios) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<FuncionarioModelCsv> unicosEmail = new ArrayList<>();
        List<FuncionarioModelCsv> unicosCracha = new ArrayList<>();
        List<FuncionarioModelCsv> unicosCpf = new ArrayList<>();

        HashMap<String, Integer> uniqueEmail = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueCracha = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueCpf = new HashMap<String, Integer>();

        int linha = 0;
        for (FuncionarioModelCsv funcionario : funcionarios) {
            linha++;

            if (!uniqueEmail.containsKey(funcionario.getEmail())) {
                uniqueEmail.put(funcionario.getEmail(), linha);
                unicosEmail.add(funcionario);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o Email já existe na linha: "
                                + uniqueEmail.get(funcionario.getEmail()))
                        .build());
                continue;
            }

            if (!uniqueCracha.containsKey(funcionario.getCracha())) {
                uniqueCracha.put(funcionario.getCracha(), linha);
                unicosCracha.add(funcionario);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o crachá já existe na linha: "
                                + uniqueCracha.get(funcionario.getCracha()))
                        .build());
                continue;
            }

            if (!uniqueCpf.containsKey(funcionario.getCpf())) {
                uniqueCpf.put(funcionario.getCpf(), linha);
                unicosCpf.add(funcionario);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o cpf já existe na linha: "
                                + uniqueCpf.get(funcionario.getCpf()))
                        .build());
                continue;
            }

            if ((!userService.checarUniqueKey(funcionario.getCpf(), funcionario.getEmail()).isEmpty())
                    || (!checaUniqueKeys(funcionario.getCracha()).isEmpty())) {
                erroValidations
                        .add(ErroValidation.builder().linha(linha).mensagem("funcionario já cadastrado").build());

            }
        }

        List<FuncionarioModelCsv> unicos = unicosEmail;
        unicos.retainAll(unicosCracha);

        return ValidationModel.<FuncionarioModelCsv>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    private FuncionarioDTO montaFuncionarioDTO(Funcionario funcionario) {
        return FuncionarioDTO.builder()
                .id(funcionario.getId())
                .cpf(funcionario.getUser().getCpf())
                .name(funcionario.getUser().getName())
                .telefone(funcionario.getUser().getTelefone())
                .email(funcionario.getUser().getEmail())
                .cracha(funcionario.getCracha())
                .area(funcionario.getArea())
                .level(funcionario.getUser().getLevel().name())
                .build();
    }

    public List<FuncionarioDTO> buscarTodosFuncionariosDTO() {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        if (funcionarios.isEmpty()) {
            throw new NoSuchElementException("Não há professores cadastradas!");
        }

        List<FuncionarioDTO> response = new ArrayList<>();

        for (Funcionario funcionario : funcionarios) {
            response.add(montaFuncionarioDTO(funcionario));
        }

        return response;
    }

    public FuncionarioDTO buscarPorIdDTO(Long id) {
        var optionalFuncionario = funcionarioRepository.findById(id);

        if (!optionalFuncionario.isPresent()) {
            throw new NoSuchElementException("Funcionário não encontrado!");
        }

        return montaFuncionarioDTO(optionalFuncionario.get());
    }

    public Funcionario buscaPorId(Long id) {
        var optionalFuncionario = funcionarioRepository.findById(id);

        if (!optionalFuncionario.isPresent()) {
            throw new NoSuchElementException("Funcionário não encontrado!");
        }

        return optionalFuncionario.get();
    }

    private List<Funcionario> checaUniqueKeys(String cracha) {
        return funcionarioRepository.findByCrachaUnique(cracha.toLowerCase());
    }

    public GenericDTO editaFuncionario(FuncionarioModel model) {
        validaEntities.validaEntrada(model);
        Funcionario funcionario = buscaPorId(model.getId());

        boolean flag = (funcionario.getUser().getEmail().equalsIgnoreCase(model.getEmail())) ? true : false;
        flag = ((funcionario.getUser().getCpf().equalsIgnoreCase(model.getCpf())) || (flag)) ? true : false;

        boolean flagFuncionario = funcionario.getCracha().equalsIgnoreCase(model.getCracha());

        funcionario.setCracha(model.getCracha());
        funcionario.setArea(model.getArea());
        funcionario.getUser().setCpf(model.getCpf());
        funcionario.getUser().setEmail(model.getEmail());
        funcionario.getUser().setPassword(passwordEncoder.encode(model.getPassword()));
        funcionario.getUser().setName(model.getName());
        funcionario.getUser().setTelefone(model.getTelefone());

        if (((flag) && (userService.checaUniqueKey(funcionario.getUser()).size() > 1)) ||
                ((!flag) && (userService.checaUniqueKey(funcionario.getUser()).size() > 0))) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Funcionario já existe").build();
        }

        if ((!flagFuncionario) && (!checaUniqueKeys(funcionario.getCracha()).isEmpty())) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Funcionario já existe").build();
        }

        funcionarioRepository.save(funcionario);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("funcionario editado com sucesso").build();
    }

    public GenericDTO excluirFuncionario(Long id) {
        Funcionario funcionario = buscaPorId(id);
        funcionarioRepository.delete(funcionario);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Funcionario deletado com sucesso").build();
    }
}
