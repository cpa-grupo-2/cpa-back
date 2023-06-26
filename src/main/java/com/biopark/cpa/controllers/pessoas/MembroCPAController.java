package com.biopark.cpa.controllers.pessoas;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.pessoas.UserDTO;
import com.biopark.cpa.form.pessoas.CadastroCPAModel;
import com.biopark.cpa.services.pessoas.MembrosCPAService;
import com.biopark.cpa.services.pessoas.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/membros-cpa")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CPA')")
public class MembroCPAController {
    private final MembrosCPAService membrosCPAService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarMembroExterno(@RequestBody CadastroCPAModel membroCPA){
        GenericDTO response = membrosCPAService.cadastrarCPA(membroCPA);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> listarMembrosCPA() {
        List<UserDTO> membrosCPA = membrosCPAService.buscarTodosMembrosCPADTO();
        return ResponseEntity.ok().body(membrosCPA);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> buscarPorId (@PathVariable(name = "id") Long id) {
        UserDTO membroCPA = userService.buscarPorIdDTO(id);
        return ResponseEntity.status(HttpStatus.OK).body(membroCPA);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarMembroCPA(@RequestBody CadastroCPAModel membrosCPA) {
        GenericDTO response = membrosCPAService.editarMembroCPA(membrosCPA);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirMembroCpa(@PathVariable Long id) {
        GenericDTO response = membrosCPAService.deleteMembroCPA(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
}
