package com.biopark.cpa.controllers.grupos;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.form.grupos.DesafioTurmaModel;
import com.biopark.cpa.services.grupos.DesafioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/DesafioTurma")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CPA')")
public class DesafioTurmaController {
    private final DesafioService desafioService;

    @PostMapping
    public ResponseEntity<GenericDTO> associarDesafioTurma(@RequestBody DesafioTurmaModel desafioTurmaModel){
        GenericDTO response = desafioService.associarDesafioTurma(desafioTurmaModel);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
