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
import com.biopark.cpa.entities.grupos.Eixo;
import com.biopark.cpa.repository.grupo.EixoRepository;
import com.biopark.cpa.services.grupos.EixoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/eixo")
public class EixoController {
    private final EixoService eixoService;
    private final EixoRepository eixoRepository;

    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarEixo(@RequestBody Eixo eixo) {
        GenericDTO response = eixoService.cadastrarEixo(eixo);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Buscar eixo por nome
    @GetMapping
    public ResponseEntity<Eixo> buscarEixoPorID(@RequestParam(name = "id") Long id) {
        Eixo eixo = eixoService.buscarEixoPorID(id);
        return ResponseEntity.status(HttpStatus.OK).body(eixo);
    }

    // Buscar todos os eixos
    @GetMapping("/eixos")
    public ResponseEntity<List<Eixo>> buscarTodosEixos() {
        List<Eixo> eixos = eixoRepository.findAll();
        if (eixos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eixos);
        }
        return ResponseEntity.status(HttpStatus.OK).body(eixos);
    }

    // Editar eixo
    @PutMapping
    public ResponseEntity<GenericDTO> editarEixo(@RequestBody Eixo eixo) {
        GenericDTO response = eixoService.editarEixo(eixo);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // Excluir eixo
    @DeleteMapping
    public ResponseEntity<GenericDTO> excluirEixo(@RequestParam("id") int idRequest) {
        Long id = Long.valueOf(idRequest);
        GenericDTO response = eixoService.excluirEixo(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
