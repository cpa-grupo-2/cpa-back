
package com.biopark.cpa.controllers.grupos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.entities.grupos.Questoes;
import com.biopark.cpa.services.grupos.QuestoesService;

@RestController
@RequestMapping("/questoes")
public class QuestoesController {
    
    private QuestoesService questoesService;
    
    @Autowired
    public QuestoesController(QuestoesService questoesService) {
        this.questoesService = questoesService;
    }
    
    @PostMapping
    public ResponseEntity<Questoes> criarQuestoes(@RequestBody Questoes questoes) {
        Questoes questaoCriada = questoesService.criarQuestoes(questoes);
        return new ResponseEntity<>(questaoCriada, HttpStatus.CREATED);
    }
}
