package com.biopark.cpa.services.grupos;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questao;
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

    // Cadastrar Questão
    public GenericDTO cadastrarQuestoes(Questao questoes) {
        Set<ConstraintViolation<Questao>> violacoes = validator.validate(questoes);

        if (!violacoes.isEmpty()) {
            String mensagem = "";
            for (ConstraintViolation<Questao> violacao : violacoes) {
                mensagem += violacao.getMessage() + "; ";
            }
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem(mensagem).build();
        }

        if (questaoRepository.findByDescricao(questoes.getDescricao()).isPresent()) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Questão já cadastrada").build();
        }
        Questao novaQuestao = Questao.builder()
                .descricao(questoes.getDescricao())
                .tipo(questoes.getTipo())
                .build();

        questaoRepository.save(novaQuestao);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Questão cadastrada com sucesso.").build();
    }

    // Filtrar as questões por descricao
    public Questao buscarQuestaoPorDescricao(String descricaoQuestao) {
        var optionalQuestoes = questaoRepository.findByDescricao(descricaoQuestao);
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
    public GenericDTO editarQuestao(Questao questaoRequest) {
        try {
            Questao questao = buscarQuestaoPorDescricao(questaoRequest.getDescricao());
            questao.setTipo(questaoRequest.getTipo());
            questaoRepository.save(questao);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Questao " + questaoRequest.getDescricao() + " editada com sucesso")
                    .build();
        } catch (Exception e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem(e.getMessage()).build();
        }
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