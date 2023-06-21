package com.biopark.cpa.services.pessoas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.cadastroCsv.ErroValidation;
import com.biopark.cpa.dto.cadastroCsv.ValidationModel;
import com.biopark.cpa.entities.pessoas.Funcionario;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.entities.user.enums.Level;
import com.biopark.cpa.entities.user.enums.Role;
import com.biopark.cpa.form.cadastroCsv.FuncionarioModel;
import com.biopark.cpa.repository.pessoas.FuncionarioRepository;
import com.biopark.cpa.repository.pessoas.UserRepository;
import com.biopark.cpa.services.security.GeneratePassword;
import com.biopark.cpa.services.utils.CsvParserService;

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

    @Transactional
    public CadastroDTO cadastrarFuncionario(List<FuncionarioModel> funcionariosModel, boolean update) {
        List<ErroValidation> errors = csvParserService.validaEntrada(funcionariosModel);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        if (!update) {

            ValidationModel<FuncionarioModel> model = checarDuplicatas(funcionariosModel);
            List<ErroValidation> duplicatas = model.getErrors();
            warnings = model.getWarnings();
            funcionariosModel = model.getObjects();

            if (!duplicatas.isEmpty()) {
                return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(duplicatas).warnings(warnings).build();
            }

            List<User> users = new ArrayList<>();
            List<Funcionario> funcionarios = new ArrayList<>();

            for (FuncionarioModel funcionarioModel : funcionariosModel) {
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

        for (FuncionarioModel funcionarioModel : funcionariosModel) {
            User user = User.builder()
                    .cpf(funcionarioModel.getCpf())
                    .name(funcionarioModel.getName())
                    .telefone(funcionarioModel.getTelefone())
                    .email(funcionarioModel.getEmail())
                    .password(generatePassword.getPwd())
                    .role(Role.FUNCIONARIO)
                    .level(Level.USER)
                    .build();

            user = userService.buscarPorCpf(funcionarioModel.getCpf());

            Funcionario funcionario = Funcionario.builder()
                    .cracha(funcionarioModel.getCracha())
                    .area(funcionarioModel.getArea())
                    .user(user)
                    .build();

            userRepository.upsert(user);
            funcionarioRepository.upsert(funcionario);
        }

        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<FuncionarioModel> checarDuplicatas(List<FuncionarioModel> funcionarios) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<FuncionarioModel> unicosEmail = new ArrayList<>();
        List<FuncionarioModel> unicosCracha = new ArrayList<>();
        List<FuncionarioModel> unicosCpf = new ArrayList<>();

        HashMap<String, Integer> uniqueEmail = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueCracha = new HashMap<String, Integer>();
        HashMap<String, Integer> uniqueCpf = new HashMap<String, Integer>();

        int linha = 0;
        for (FuncionarioModel funcionario : funcionarios) {
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

            if (userRepository.findByEmail(funcionario.getEmail()).isPresent()
                    | userRepository.findByCpf(funcionario.getCpf()).isPresent()
                    | funcionarioRepository.findByCracha(funcionario.getCracha()).isPresent()) {
                erroValidations
                        .add(ErroValidation.builder().linha(linha).mensagem("funcionario já cadastrado").build());
            }
        }

        List<FuncionarioModel> unicos = unicosEmail;
        unicos.retainAll(unicosCracha);

        return ValidationModel.<FuncionarioModel>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    // Filtrar Funcionário por id
    public Funcionario buscarPorID(Long id) {
        var optionalFuncionario = funcionarioRepository.findById(id);

        if (optionalFuncionario.isPresent()) {
            return optionalFuncionario.get();
        } else {
            throw new NoSuchElementException("Funcionário não encontrado!");
        }
    }

    // Filtrar Funcionário por crachá
    public Funcionario buscarPorCracha(String cracha) {
        var optionalFuncionario = funcionarioRepository.findByCracha(cracha);

        if (optionalFuncionario.isPresent()) {
            return optionalFuncionario.get();
        } else {
            throw new NoSuchElementException("Funcionário não encontrado!");
        }
    }

    // Filtrar todos os Funcionários
    public List<Funcionario> buscarTodosFuncionarios() {
        var funcionario = funcionarioRepository.findAll();
        if (funcionario.isEmpty()) {
            throw new NoSuchElementException("Não há professores cadastradas!");
        }
        return funcionario;
    }

    // Editar Funcionário
    public GenericDTO editarFuncionario(Funcionario funcionarioRequest) {
        try {
            Funcionario funcionario = buscarPorCracha(funcionarioRequest.getCracha());
            funcionario.getUser().setName(funcionarioRequest.getUser().getName());
            funcionario.getUser().setTelefone(funcionarioRequest.getUser().getTelefone());
            funcionario.getUser().setEmail(funcionarioRequest.getUser().getEmail());
            funcionarioRepository.save(funcionario);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Funcionário " + funcionarioRequest.getCracha() + "editado com sucesso")
                    .build();
        } catch (Exception e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem(e.getMessage()).build();
        }
    }

    // Excluir Funcionário
    public GenericDTO excluirFuncionario(Long id) {
        try {
            var funcionarioDB = funcionarioRepository.findById(id);
            if (!funcionarioDB.isPresent()) {
                return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("funcionário não encontrado").build();
            }
            Funcionario funcionario = funcionarioDB.get();
            funcionarioRepository.delete(funcionario);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Funcionario " + funcionario.getId() + " excluído com sucesso")
                    .build();
        } catch (EmptyResultDataAccessException e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND)
                    .mensagem("Funcionario " + id + " não encontrado")
                    .build();
        }
    }
}