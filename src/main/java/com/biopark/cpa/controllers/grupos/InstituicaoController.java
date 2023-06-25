package com.biopark.cpa.controllers.grupos;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.grupos.InstituicaoDTO;
import com.biopark.cpa.entities.grupos.Instituicao;
import com.biopark.cpa.form.grupos.InstituicaoModel;
import com.biopark.cpa.services.grupos.InstituicaoService;
import com.biopark.cpa.services.utils.CsvParserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/instituicao")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CPA')")
public class InstituicaoController {

    private final CsvParserService csvParserService;
    private final InstituicaoService instituicaoService;

    @PostMapping
    public ResponseEntity<CadastroDTO> cadastrarInstituicao(@RequestParam("file") MultipartFile file,
            @RequestParam(name = "update") Boolean update) throws IOException {
        List<Instituicao> instituicoes = csvParserService.parseCsv(file, Instituicao.class);
        CadastroDTO cadastroDTO = instituicaoService.cadastrarInstituicao(instituicoes, update);
        return ResponseEntity.status(cadastroDTO.getStatus()).body(cadastroDTO);
    }

    @GetMapping("codigo/{codInstituicao}")
    public ResponseEntity<InstituicaoDTO> buscarCodigoInstituicao(
            @PathVariable("codInstituicao") String codigoInstituicao) {
        InstituicaoDTO instituicao = instituicaoService.buscarPorCodigoDTO(codigoInstituicao);
        return ResponseEntity.status(HttpStatus.OK).body(instituicao);
    }

    @GetMapping
    public ResponseEntity<List<InstituicaoDTO>> buscarTodasInstituicoes() {
        List<InstituicaoDTO> instituicoes = instituicaoService.buscarTodasInstituicoesDTO();
        return ResponseEntity.ok().body(instituicoes);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarInstituicao(@RequestBody InstituicaoModel instituicao) {
        GenericDTO response = instituicaoService.editarInstituicao(instituicao);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirInstituicao(@PathVariable("id") Long id) {
        GenericDTO response = instituicaoService.excluirInstituicao(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
