package com.biopark.cpa.services.grupos;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.repository.grupo.QuestoesRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class QuestoesService {
    private QuestoesRepository questoesRepository;

   
    // Cadastrar Questão
    public GenericDTO cadastrarQuestoes(Questoes questoes) {
        if ((questoesRepository.findByDescricao(questoes.getDescricao()).isPresent())) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Questão já cadastrada").build();
        }
        Questoes novaQuestao = Questoes.builder()
                .descricao(questoes.getDescricao())
                .tipo(questoes.getTipo())
                .build();
        questoesRepository.save(novaQuestao);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Questão cadastrada com sucesso.").build();
    }

    // Filtrar as questões por descricao
    public Questoes buscarQuestaoPorDescricao(String descricaoQuestao) {
        var optionalQuestoes = questoesRepository.findByDescricao(descricaoQuestao);
        if (optionalQuestoes.isPresent()) {
            return optionalQuestoes.get();
        } else {
            throw new NoSuchElementException("Questão não encontrada!");
        }
    }

    // Filtrar todas as questões
    public List<Questoes> buscarTodasQuestoes() {
        var questoes = questoesRepository.findAll();
        if (questoes.isEmpty()) {
            throw new RuntimeException("Não há questões cadastradas!");
        }
        return questoes;
    }
}