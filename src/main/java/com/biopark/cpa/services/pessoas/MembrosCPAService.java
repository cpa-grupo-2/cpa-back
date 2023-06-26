package com.biopark.cpa.services.pessoas;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.pessoas.UserDTO;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.entities.user.enums.Level;
import com.biopark.cpa.entities.user.enums.Role;
import com.biopark.cpa.form.pessoas.CadastroCPAModel;
import com.biopark.cpa.repository.pessoas.UserRepository;
import com.biopark.cpa.services.security.GeneratePassword;
import com.biopark.cpa.services.utils.ValidaEntities;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembrosCPAService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final GeneratePassword generatePassword;
    private final ValidaEntities validaEntities;

    public GenericDTO cadastrarCPA(CadastroCPAModel usuarioCPA) {
        validaEntities.validaEntrada(usuarioCPA);

        User user = User.builder()
                .name(usuarioCPA.getName())
                .cpf(usuarioCPA.getCpf())
                .email(usuarioCPA.getEmail())
                .password(generatePassword.getPwd())
                .telefone(usuarioCPA.getTelefone())
                .level(Level.CPA)
                .role(Role.EXTERNO)
                .build();

        if (!userService.checaUniqueKeys(user).isEmpty()) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Este usuario já existe").build();
        }

        userRepository.save(user);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Usuário cadastrado com sucesso.").build();
    }
    
    public List<UserDTO> buscarTodosMembrosCPADTO() {
        List<User> users = userRepository.findAllByLevel(Level.CPA.name());
        if (users.isEmpty()) {
            throw new NoSuchElementException("Não há Membros CPA cadastrados!");
        }

        List<UserDTO> membrosCPA = new ArrayList<>();
        for (User user: users) {
            membrosCPA.add(
                UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .cpf(user.getCpf())
                .telefone(user.getTelefone())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build()
            );
        }
        return membrosCPA;
    }

    public GenericDTO editarMembroCPA(CadastroCPAModel model){
        validaEntities.validaEntrada(model);
        User user = userService.buscarPorId(model.getId());

        if (user.getLevel() != Level.CPA) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("usuário informado não é membro cpa").build();
        }

        boolean flag = ((user.getCpf().equals(model.getCpf()))|(user.getEmail().equals(model.getEmail()))) ? true : false;        

        user.setCpf(model.getCpf());
        user.setEmail(model.getEmail());
        user.setName(model.getName());
        user.setTelefone(model.getTelefone());

        if ((flag && userService.checaUniqueKey(user).size() > 1)||((!flag)&&(!userService.checaUniqueKey(user).isEmpty()))) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("usuário já existe").build();
        }

        userRepository.save(user);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Membro CPA editado com sucesso").build();
    }

    public GenericDTO deleteMembroCPA(Long id){
        User user = userService.buscarPorId(id);
        if (user.getLevel()!=Level.CPA) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("usuário informado não é membro cpa").build();
        }

        userRepository.delete(user);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Membro deletado com sucesso").build();
    }
}
