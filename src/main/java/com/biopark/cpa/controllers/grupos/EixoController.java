package com.biopark.cpa.controllers.grupos;

import java.util.List;

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
import com.biopark.cpa.dto.grupos.EixoDTO;
import com.biopark.cpa.entities.grupos.Eixo;
import com.biopark.cpa.form.grupos.EixoModel;
import com.biopark.cpa.services.grupos.EixoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/eixo")
public class EixoController {
    private final EixoService eixoService;

    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarEixo(@RequestBody Eixo eixo) {
        GenericDTO response = eixoService.cadastrarEixo(eixo);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EixoDTO>> buscarTodosEixos() {
        List<EixoDTO> eixos = eixoService.buscarTodosEixosDTO();
        return ResponseEntity.ok().body(eixos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EixoDTO> buscarId(@PathVariable("id") Long id){
        EixoDTO eixo = eixoService.buscarEixoIdDTO(id);
        return ResponseEntity.ok().body(eixo);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarEixo(@RequestBody EixoModel model) {
        GenericDTO response = eixoService.editar(model);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirEixo(@PathVariable("id") Long id) {
        GenericDTO response = eixoService.excluirEixo(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
