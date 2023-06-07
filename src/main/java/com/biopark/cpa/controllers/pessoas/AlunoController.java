package com.biopark.cpa.controllers.pessoas;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.form.cadastroCsv.AlunoModel;
import com.biopark.cpa.services.pessoas.AlunoService;
import com.biopark.cpa.services.utils.CsvParserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/aluno")
@PreAuthorize("hasRole('CPA')")
public class AlunoController {
    
    private final CsvParserService csvParserService;
    private final AlunoService alunoService;

    @PostMapping
    public ResponseEntity<CadastroDTO> cadastrarAluno(@RequestParam("file") MultipartFile file, @RequestParam("update") Boolean update) throws IOException{
        List<AlunoModel> alunos = csvParserService.parseCsv(file, AlunoModel.class);
        CadastroDTO response = alunoService.cadastrarAluno(alunos, update);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
}
