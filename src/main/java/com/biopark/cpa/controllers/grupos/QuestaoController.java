package com.biopark.cpa.controllers.grupos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.grupos.QuestaoDTO;
import com.biopark.cpa.form.grupos.QuestaoEditModel;
import com.biopark.cpa.form.grupos.QuestaoModel;
import com.biopark.cpa.services.grupos.QuestaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/questao")
public class QuestaoController {
    private final QuestaoService questaoService;

    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarQuestao(@RequestBody QuestaoModel questao) {
        GenericDTO response = questaoService.cadastrarQuestoes(questao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
   
    @GetMapping
    public ResponseEntity<List<QuestaoDTO>> buscarTodos(){
        List<QuestaoDTO> questoes = questaoService.buscarTodasQuestoesDTO();
        return ResponseEntity.ok().body(questoes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestaoDTO> buscarQuestaoPorID(@PathVariable(name = "id") Long id) {
        QuestaoDTO questao = questaoService.buscarIdDTO(id);
        return ResponseEntity.status(HttpStatus.OK).body(questao);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarQuestao(@RequestBody QuestaoEditModel model) {
        GenericDTO response = questaoService.editarQuestao(model);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirQuestao(@PathVariable("id") Long id) {
        GenericDTO response = questaoService.excluirQuestao(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
