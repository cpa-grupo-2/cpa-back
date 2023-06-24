package com.biopark.cpa.services.grupos;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questao;
import com.biopark.cpa.entities.grupos.enums.TipoQuestao;
import com.biopark.cpa.form.grupos.QuestaoModel;
import com.biopark.cpa.repository.grupo.EixoRepository;
import com.biopark.cpa.repository.grupo.QuestaoRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestaoService {
    private final QuestaoRepository questaoRepository;
    private final Validator validator;
    private final EixoRepository eixoRepository;

    // Cadastrar Questão
    public GenericDTO cadastrarQuestoes(QuestaoModel questao) {
        Set<ConstraintViolation<QuestaoModel>> violacoes = validator.validate(questao);

        if (!violacoes.isEmpty()) {
            String mensagem = "";
            for (ConstraintViolation<QuestaoModel> violacao : violacoes) {
                mensagem += violacao.getMessage() + "; ";
            }
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem(mensagem).build();
        }

        var eixo = eixoRepository.findById(questao.getEixoId());

        if(!eixo.isPresent()) {
           return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("Eixo informado não encontrado!").build();
        }

        if (questaoRepository.findByDescricao(questao.getDescricao()).isPresent()) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Questão já cadastrada").build();
        }

        TipoQuestao tipo;

        try {
            tipo = TipoQuestao.valueOf(questao.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("Tipo de questão inválido").build();
        }

        Questao novaQuestao = Questao.builder()
                .descricao(questao.getDescricao())
                .tipo(tipo)
                .eixo(eixo.get())
                .build();

        questaoRepository.save(novaQuestao);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Questão cadastrada com sucesso.").build();
    }

    // Filtrar as questões por descricao
    public Questao buscarQuestaoPorID(Long id) {
        var optionalQuestoes = questaoRepository.findById(id);
        if (optionalQuestoes.isPresent()) {
            return optionalQuestoes.get();
        } else {
            throw new NoSuchElementException("Questão não encontrada!");
        }
    }

    // Filtrar todas as questões
    public List<Questao> buscarTodasQuestoes() {
        var questoes = questaoRepository.findAll();
        if (questoes.isEmpty()) {
            throw new NoSuchElementException("Não há questões cadastradas!");
        }
        return questoes;
    }

    // Editar Questão
    public GenericDTO editarQuestao(QuestaoModel questaoRequest) {
        if (questaoRequest.getId() == null) {
            throw new IllegalArgumentException();
        }

        TipoQuestao tipo;
        try {
            tipo = TipoQuestao.valueOf(questaoRequest.getTipo().toUpperCase());
        } catch (IllegalArgumentException e) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("Tipo de questão inválido").build();
        }

        Questao questao = buscarQuestaoPorID(questaoRequest.getId());
        questao.setTipo(tipo);
        questao.setDescricao(questaoRequest.getDescricao());
        


        questaoRepository.save(questao);
        return GenericDTO.builder().status(HttpStatus.OK)
                .mensagem("Questao " + questaoRequest.getDescricao() + " editada com sucesso")
                .build();
    }

    // Excluir Questão
    public GenericDTO excluirQuestao(Long id) {
        try {
            var questaoDB = questaoRepository.findById(id);
            if (!questaoDB.isPresent()) {
                return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("Questão não encontrada").build();
            }
            Questao questao = questaoDB.get();
            questaoRepository.delete(questao);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Questao " + questao.getDescricao() + " excluída com sucesso")
                    .build();
        } catch (EmptyResultDataAccessException e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND)
                    .mensagem("Questao " + id + " não encontrada")
                    .build();
        }
    }

}