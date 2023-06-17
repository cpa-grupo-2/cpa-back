package com.biopark.cpa.controllers.grupos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.repository.grupo.QuestoesRepository;
import com.biopark.cpa.services.grupos.QuestoesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/questao")
public class QuestoesController {

    private final QuestoesService questoesService;
    private final QuestoesRepository questoesRepository;

    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarQuestao(@RequestBody Questoes questao) {
        GenericDTO response = questoesService.cadastrarQuestoes(questao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //Buscar questão por descrição
    @GetMapping
    public ResponseEntity<Questoes> buscarQuestaoPorDescricao(@RequestParam(name = "descricao") String descricaoQuestao) {
        Questoes questao = questoesService.buscarQuestaoPorDescricao(descricaoQuestao);
        return ResponseEntity.status(HttpStatus.OK).body(questao);
    }

    //Buscar  todas as questões
    @GetMapping("/questoes")
    public ResponseEntity<List<Questoes>> buscarTodasQuestoes() {
        List<Questoes> questoes = questoesRepository.findAll();
        if (questoes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(questoes);
        }
        return ResponseEntity.status(HttpStatus.OK).body(questoes);
    }
}
