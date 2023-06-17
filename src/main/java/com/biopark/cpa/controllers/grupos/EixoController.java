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
import com.biopark.cpa.entities.grupos.Eixo;
import com.biopark.cpa.repository.grupo.EixoRepository;
import com.biopark.cpa.services.grupos.EixoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/eixo")
public class EixoController {
    private EixoService eixoService;
    private EixoRepository eixoRepository;

    @PostMapping
    public ResponseEntity<GenericDTO> cadastrarEixo(@RequestBody Eixo eixo) {
        GenericDTO response = eixoService.cadastrarEixo(eixo);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    //Buscar eixo por nome
    @GetMapping
    public ResponseEntity<Eixo> buscarEixoPorNome(@RequestParam(name = "nomeEixo") String nomeEixo) {
       Eixo eixo = eixoService.buscarEixoPorNome(nomeEixo);
        return ResponseEntity.status(HttpStatus.OK).body(eixo);
    }

    //Buscar todos os eixos
    @GetMapping("/eixos")
    public ResponseEntity<List<Eixo>> buscarTodosEixos() {
        var eixo = eixoRepository.findAll();
        if (eixo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(eixo);
        }
        return ResponseEntity.status(HttpStatus.OK).body(eixo);
    }
}
