package com.biopark.cpa.services.grupos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.repository.grupo.QuestoesRepository;

@Service
public class QuestoesService {
    private QuestoesRepository questoesRepository;

    @Autowired
    public QuestoesService(QuestoesRepository questoesRepository) {
        this.questoesRepository = questoesRepository;
    }

    public Questoes criarQuestoes(Questoes questoes) {

        return questoesRepository.save(questoes);
    }

}