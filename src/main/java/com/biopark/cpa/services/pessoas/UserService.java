package com.biopark.cpa.services.pessoas;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.pessoas.UserDTO;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.repository.pessoas.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> checaUniqueKeys(User user){
        return userRepository.findUniqueKeys(user);
    }

    public List<User> checaUniqueKey(User user){
        return userRepository.findUniqueKeys(user);
    }

    public List<User> checarUniqueKey(String cpf, String email){
        return userRepository.findUniqueKeys(cpf.toLowerCase(), email.toLowerCase());
    }

    public UserDTO buscarPorIdDTO(Long id) {
        var optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new NoSuchElementException("Usuário não encontrado!");
        }

        return montaUserDTO(optionalUser.get());
    }

    public User buscarPorId(Long id) {
        var optionalUser = userRepository.findById(id);

        if (!optionalUser.isPresent()) {
            throw new NoSuchElementException("Usuário não encontrado!");
        }

        return optionalUser.get();
    }

    public User buscarPorCpf(String cpf){
        var db = userRepository.findByCpf(cpf);
        if (!db.isPresent()) {
            throw new NoSuchElementException("Usuário não encontrado");
        }

        return db.get();
    }

    public User buscarPorEmail(String email){
        var db = userRepository.findByEmail(email);
        if (!db.isPresent()) {
            throw new NoSuchElementException("Usuário não encontrado");
        }

        return db.get();
    }

    private UserDTO montaUserDTO(User user){
        return UserDTO.builder().
            id(user.getId())
            .name(user.getName())
            .cpf(user.getCpf())
            .telefone(user.getTelefone())
            .email(user.getEmail())
            .role(user.getRole().name())
            .build();
    }
    
}
