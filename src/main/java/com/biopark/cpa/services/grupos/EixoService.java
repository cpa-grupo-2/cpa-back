package com.biopark.cpa.services.grupos;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import jakarta.validation.Validator;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Eixo;
import com.biopark.cpa.repository.grupo.EixoRepository;

import jakarta.validation.ConstraintViolation;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EixoService {
    private final EixoRepository eixoRepository;
    private final Validator validator;

    // Cadastrar Eixo
    public GenericDTO cadastrarEixo(Eixo eixo) {
        Set<ConstraintViolation<Eixo>> violacoes = validator.validate(eixo);

        if (!violacoes.isEmpty()) {
            String mensagem = "";
            for (ConstraintViolation<Eixo> violacao : violacoes) {
                mensagem += violacao.getMessage() + "; ";
            }
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem(mensagem).build();
        }

        if (eixoRepository.findByNomeEixo(eixo.getNomeEixo()).isPresent()) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Eixo já cadastrado").build();
        }

        Eixo novoEixo = Eixo.builder()
                .nomeEixo(eixo.getNomeEixo())
                .descricao(eixo.getDescricao())
                .build();

        eixoRepository.save(novoEixo);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Eixo cadastrado com sucesso.").build();
    }

    // Filtrar eixo por nome
    public Eixo buscarEixoPorNome(String nomeEixo) {
        var optionalEixo = eixoRepository.findByNomeEixo(nomeEixo);
        if (optionalEixo.isPresent()) {
            return optionalEixo.get();
        } else {
            throw new NoSuchElementException("Eixo não encontrado!");
        }
    }

    // Filtrar todos os eixos
    public List<Eixo> buscarTodosEixos() {
        var eixos = eixoRepository.findAll();
        if (eixos.isEmpty()) {
            throw new NoSuchElementException("Não há eixos cadastrados!");
        }
        return eixos;
    }

    //Editar eixo
    public GenericDTO editarEixo(Eixo eixoRequest) {
        try {
            Eixo eixo = buscarEixoPorNome(eixoRequest.getNomeEixo());
            eixo.setNomeEixo(eixoRequest.getNomeEixo());
            eixo.setDescricao(eixoRequest.getDescricao());
            eixoRepository.save(eixo);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Eixo " + eixoRequest.getNomeEixo() + " editado com sucesso")
                    .build();
        } catch (Exception e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem(e.getMessage()).build();
        }
    }

    //Excluir eixo
    public GenericDTO excluirEixo(Long id) {
        try {
            var eixoDB = eixoRepository.findById(id);
            if (!eixoDB.isPresent()) {
                return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("eixo não encontrado").build();
            }
            Eixo eixo = eixoDB.get();
            eixoRepository.delete(eixo);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Eixo " + eixo.getNomeEixo() + " excluído com sucesso")
                    .build();
        } catch (EmptyResultDataAccessException e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND)
                    .mensagem("Eixo " + id + " não encontrado")
                    .build();
        }
    }
}
