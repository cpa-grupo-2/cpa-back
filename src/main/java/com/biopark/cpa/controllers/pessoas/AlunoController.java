package com.biopark.cpa.controllers.pessoas;

import java.io.IOException;
import java.util.List;

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
import com.biopark.cpa.dto.pessoas.AlunoDTO;
import com.biopark.cpa.form.cadastroCsv.AlunoModelCsv;
import com.biopark.cpa.form.pessoas.AlunoModel;
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
        List<AlunoModelCsv> alunos = csvParserService.parseCsv(file, AlunoModelCsv.class);
        CadastroDTO response = alunoService.cadastrarAluno(alunos, update);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AlunoDTO>> buscarTodos(){
        List<AlunoDTO> alunos = alunoService.buscarTodosDTO();
        return ResponseEntity.ok().body(alunos);
    }

    @GetMapping("/{RA}")
    public ResponseEntity<AlunoDTO> buscarPorRA(@PathVariable("RA") String ra){
        AlunoDTO aluno = alunoService.buscarPorRADTO(ra);
        return ResponseEntity.ok().body(aluno);
    }

    @GetMapping("/turma/{codTurma}")
    public ResponseEntity<List<AlunoDTO>> buscarPorTurma(@PathVariable("codTurma") String codTurma){
        List<AlunoDTO> alunos = alunoService.buscarPorTurmaDTO(codTurma);
        return ResponseEntity.ok().body(alunos);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarAluno(@RequestBody AlunoModel model){
        GenericDTO response = alunoService.editar(model);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> deletar(@PathVariable("id") Long id){
        GenericDTO response = alunoService.deletar(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
