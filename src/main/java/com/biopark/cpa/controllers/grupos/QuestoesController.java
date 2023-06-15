package com.biopark.cpa.controllers.grupos;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.form.pessoas.CadastroCPA;
import com.biopark.cpa.repository.grupo.QuestoesRepository;
import com.biopark.cpa.services.grupos.QuestoesService;

@RestController
@RequestMapping("api/questao")
public class QuestoesController {

    private QuestoesService questoesService;
    private QuestoesRepository questoesRepository;

  
    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarQuestao(@RequestBody Questoes questao){
        GenericDTO response = questoesService.cadastrarQuestoes(questao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }


    @GetMapping
    public ResponseEntity<Optional<Questoes>> buscarCodigoQuestao(
            @RequestParam(name = "codigoQuestao") String codigoQuestao) {
        var questao = questoesRepository.findByCodigoQuestao(codigoQuestao);
        if (questao == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(questao);
        }
        return ResponseEntity.status(HttpStatus.OK).body(questao);
    }

    @GetMapping("/questoes")
    public ResponseEntity<List<Questoes>> buscarTodasQuestoes() {
        var questoes = questoesRepository.findAll();
        if (questoes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(questoes);
        }
        return ResponseEntity.status(HttpStatus.OK).body(questoes);
    }
}
