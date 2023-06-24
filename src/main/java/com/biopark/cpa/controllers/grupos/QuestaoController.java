package com.biopark.cpa.controllers.grupos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questao;
import com.biopark.cpa.form.grupos.QuestaoModel;
import com.biopark.cpa.repository.grupo.QuestaoRepository;
import com.biopark.cpa.services.grupos.QuestaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/questao")
public class QuestaoController {

    private final QuestaoService questaoService;
    private final QuestaoRepository questaoRepository;

    //Cadastrar Questão
    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarQuestao(@RequestBody QuestaoModel questao) {
        GenericDTO response = questaoService.cadastrarQuestoes(questao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    //Buscar Questão por ID
    @GetMapping
    public ResponseEntity<Questao> buscarQuestaoPorID(@RequestParam(name = "id") Long id) {
        Questao questao = questaoService.buscarQuestaoPorID(id);
        return ResponseEntity.status(HttpStatus.OK).body(questao);
    }

    //Buscar Todas as Questões
    @GetMapping("/questoes")
    public ResponseEntity<List<Questao>> buscarTodasQuestoes() {
        List<Questao> questoes = questaoRepository.findAll();
        if (questoes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(questoes);
        }
        return ResponseEntity.status(HttpStatus.OK).body(questoes);
    }

     // Editar Questão
    @PutMapping
    public ResponseEntity<GenericDTO> editarQuestao(@RequestBody QuestaoModel questao) {
        GenericDTO response = questaoService.editarQuestao(questao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Excluir Questão
    @DeleteMapping
    public ResponseEntity<GenericDTO> excluirQuestao(@RequestParam("id") int idRequest) {
        Long id = Long.valueOf(idRequest);
        GenericDTO response = questaoService.excluirQuestao(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
