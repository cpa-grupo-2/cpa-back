package com.biopark.cpa.services.grupos;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.repository.grupo.QuestoesRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestoesService {
    private final QuestoesRepository questoesRepository;
    private final Validator validator;

    // Cadastrar Questão
    public GenericDTO cadastrarQuestoes(Questoes questoes) {
    Set<ConstraintViolation<Questoes>> violacoes = validator.validate(questoes);

    if (!violacoes.isEmpty()) {
        String mensagem = "";
        for (ConstraintViolation<Questoes> violacao : violacoes) {
            mensagem += violacao.getMessage() + "; ";
        }
        return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem(mensagem).build();
    }

    if (questoesRepository.findByDescricao(questoes.getDescricao()).isPresent()) {
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
            throw new NoSuchElementException("Não há questões cadastradas!");
        }
        return questoes;
    }
}