package com.biopark.cpa.services.grupos;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Eixo;
import com.biopark.cpa.repository.grupo.EixoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EixoService {
    private final EixoRepository eixoRepository;

    // Cadastrar Eixo
    public GenericDTO cadastrarEixo(Eixo eixo) {
        if ((eixoRepository.findByNomeEixo(eixo.getNomeEixo()).isPresent())) {
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
            throw new NoSuchElementException("Eixo não encontrada!");
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
}
