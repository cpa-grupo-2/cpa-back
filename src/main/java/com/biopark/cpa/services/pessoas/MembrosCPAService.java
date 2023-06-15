package com.biopark.cpa.services.pessoas;

import java.util.List;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.user.User;
import com.biopark.cpa.entities.user.enums.Level;
import com.biopark.cpa.entities.user.enums.Role;
import com.biopark.cpa.form.pessoas.CadastroCPA;
import com.biopark.cpa.repository.pessoas.FuncionarioRepository;
import com.biopark.cpa.repository.pessoas.MembrosCPARepository;
import com.biopark.cpa.repository.pessoas.UserRepository;
import com.biopark.cpa.services.security.GeneratePassword;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembrosCPAService {
    private final UserRepository userRepository;
    private final Validator validator;
    private final GeneratePassword generatePassword;
    private final MembrosCPARepository membrosCPARepository;

    public GenericDTO cadastrarCPA(CadastroCPA usuarioCPA) {
        Set<ConstraintViolation<CadastroCPA>> violacoes = validator.validate(usuarioCPA);

        if (!violacoes.isEmpty()) {
            String mensagem = "";
            for (ConstraintViolation<CadastroCPA> violacao : violacoes) {
                mensagem += violacao.getMessage() + "; ";
            }
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem(mensagem).build();
        }

        if ((userRepository.findByEmail(usuarioCPA.getEmail()).isPresent())
                | (userRepository.findByCpf(usuarioCPA.getCpf()).isPresent())) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Usuario já cadastrado").build();
        }

        User user = User.builder()
                .name(usuarioCPA.getName())
                .cpf(usuarioCPA.getCpf())
                .email(usuarioCPA.getEmail())
                .password(generatePassword.getPwd())
                .telefone(usuarioCPA.getTelefone())
                .level(Level.CPA)
                .role(Role.EXTERNO)
                .build();

        userRepository.save(user);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Usuário cadastrado com sucesso.").build();
    }

      /* 
    // Filtrar Membro CPA por id
    public CadastroCPA buscarPorID(Long id) {
        var optionalMembrosCPA = MembrosCPARepository.findById(id);
        if (optionalMembrosCPA.isPresent()) {
            return optionalMembrosCPA.get();
        } else {
            throw new RuntimeException("Membros CPA não encontrado!");

        }
    }

    // Filtrar todos os Membros CPA
    public List<CadastroCPA> buscarTodosMembrosCPA() {
        var membrosCPA = MembrosCPARepository.findAll();
        if (membrosCPA.isEmpty()) {
            throw new RuntimeException("Não há Membros CPA cadastrados!");
        }
        return membrosCPA;
    }

    // Editar Membro CPA
    public GenericDTO editarMembroCPA(CadastroCPA membrosCPARequest) {
        try {
            CadastroCPA membrosCPA = buscarPorID(membrosCPARequest.getId());
            membrosCPA.getUser().setName(membrosCPARequest.getUser().getName());
            membrosCPA.getUser().setTelefone(membrosCPARequest.getUser().getTelefone());
            membrosCPA.getUser().setEmail(membrosCPARequest.getUser().getEmail());
            membrosCPARepository.save(membrosCPA);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Membro CPA " + membrosCPARequest.getId() + "editado com sucesso")
                    .build();
        } catch (Exception e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem(e.getMessage()).build();
        }
    }

    // Excluir Membro CPA
    public GenericDTO excluirMembroCPA(Long id) {
        try {
            var membroCPADB = MembrosCPARepository.findById(id);
            if (!membroCPADB.isPresent()) {
                return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("membro CPA não encontrado").build();
            }
            CadastroCPA membrosCPA = membroCPADB.get();
            MembrosCPARepository.delete(CadastroCPA);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Membro CPA " + membrosCPA.getId() + " excluído com sucesso")
                    .build();
        } catch (EmptyResultDataAccessException e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND)
                    .mensagem("Membro CPA " + id + " não encontrado")
                    .build();
        }
    }
*/
}
