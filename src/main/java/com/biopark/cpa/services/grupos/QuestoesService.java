package com.biopark.cpa.services.grupos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.repository.grupo.QuestoesRepository;

import java.util.List;

@Service
public class QuestoesService {
    private QuestoesRepository questoesRepository;

    @Autowired
    public QuestoesService(QuestoesRepository questoesRepository) {
        this.questoesRepository = questoesRepository;
    }

    /* public Questoes criarQuestoes(Questoes questoes) {
        return questoesRepository.save(questoes);
    }
     */

    public Questoes criarQuestao(String titulo, String descricao, String resposta) {
        Questoes questao = new Questoes();
        questao.setTitulo(titulo);
        questao.setDescricao(descricao);
        questao.setResposta(resposta);
        return questoesRepository.save(questao);
    }

    //Filtrar as questões por código
    public Questoes buscarQuestaoPorCodigo(String codigoQuestao) {
        var optionalQuestoes = questoesRepository.findByCodigoQuestao(codigoQuestao);
        if (optionalQuestoes.isPresent()) {
            return optionalQuestoes.get();
        } else {
            throw new RuntimeException("Questão não encontrada!");
        }
    }

    //Filtrar todas as questões
    public List<Questoes> buscarTodasQuestoes() {
        var questoes = questoesRepository.findAll();
        if (questoes.isEmpty()) {
        throw new RuntimeException("Não há questões cadastradas!");
        }
        return questoes;
        }
}
