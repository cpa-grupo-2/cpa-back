package com.biopark.cpa.services.grupos;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.repository.grupo.QuestoesRepository;

import java.util.List;

@Service
public class QuestoesService {
    private QuestoesRepository questoesRepository;

   
    // Cadastrar Questão
    public GenericDTO cadastrarQuestoes(Questoes questoes) {
        if ((questoesRepository.findByCodigoQuestao(questoes.getCodigoQuestao()).isPresent())) {
            return GenericDTO.builder().status(HttpStatus.CONFLICT).mensagem("Questão já cadastrada").build();
        }

        Questoes novaQuestao = Questoes.builder()
                .descricao(questoes.getDescricao())
                .titulo(questoes.getTitulo())
                .resposta(questoes.getResposta())
                .build();
        questoesRepository.save(novaQuestao);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Questão cadastrada com sucesso.").build();
    }

    // Filtrar as questões por código
    public Questoes buscarQuestaoPorCodigo(String codigoQuestao) {
        var optionalQuestoes = questoesRepository.findByCodigoQuestao(codigoQuestao);
        if (optionalQuestoes.isPresent()) {
            return optionalQuestoes.get();
        } else {
            throw new RuntimeException("Questão não encontrada!");
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