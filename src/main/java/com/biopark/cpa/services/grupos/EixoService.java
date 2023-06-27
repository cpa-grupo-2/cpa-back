package com.biopark.cpa.services.grupos;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.grupos.EixoDTO;
import com.biopark.cpa.entities.grupos.Eixo;
import com.biopark.cpa.form.grupos.EixoModel;
import com.biopark.cpa.repository.grupo.EixoRepository;
import com.biopark.cpa.services.utils.ValidaEntities;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EixoService {
    private final EixoRepository eixoRepository;
    private final ValidaEntities validaEntities;

    public GenericDTO cadastrarEixo(Eixo model) {
        validaEntities.validaEntrada(model);

        if (!uniqueKeys(model.getNomeEixo()).isEmpty()) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Eixo já cadastrado").build();
        }

        Eixo eixo = Eixo.builder()
                .nomeEixo(model.getNomeEixo())
                .descricao(model.getDescricao())
                .build();

        eixoRepository.save(eixo);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Eixo cadastrado com sucesso.").build();
    }

    public List<Eixo> uniqueKeys(String nomeEixo){
        return eixoRepository.findByUniqueKeys(nomeEixo.toLowerCase());
    }

    private EixoDTO montaDTO(Eixo eixo){
        return EixoDTO.builder()
            .id(eixo.getId())
            .nomeEixo(eixo.getNomeEixo())
            .descricao(eixo.getDescricao())
            .build();
    }
    public List<EixoDTO> buscarTodosEixosDTO() {
        List<Eixo> eixos = eixoRepository.findAll();
        if (eixos.isEmpty()) {
            throw new NoSuchElementException("Não há eixos cadastrados!");
        }

        List<EixoDTO> response = new ArrayList<>();

        for (Eixo eixo : eixos) {
            response.add(montaDTO(eixo));
        }
        return response;
    }

    public EixoDTO buscarEixoIdDTO(Long id){
        var op = eixoRepository.findById(id);
        if (!op.isPresent()) {
            throw new NoSuchElementException("Eixo não encontrado");
        }

        return montaDTO(op.get());
    }

    public Eixo buscarEixoId(Long id){
        var op = eixoRepository.findById(id);
        if (!op.isPresent()) {
            throw new NoSuchElementException("Eixo não encontrado");
        }

        return op.get();
    }

    public GenericDTO editar(EixoModel model){
        Eixo eixo = buscarEixoId(model.getId());

        boolean flag = (model.getNomeEixo().equalsIgnoreCase(eixo.getNomeEixo())) ? true : false;

        if (!flag && (!uniqueKeys(model.getNomeEixo()).isEmpty())) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Eixo já cadastrado").build();
        }

        eixo.setNomeEixo(model.getNomeEixo());
        eixo.setDescricao(model.getDescricao());

        eixoRepository.save(eixo);

        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Eixo atualizado com sucesso").build();
    }

    public GenericDTO excluirEixo(Long id){
        Eixo eixo = buscarEixoId(id);
        eixoRepository.delete(eixo);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Eixo excluido com sucesso").build();
    }
}
