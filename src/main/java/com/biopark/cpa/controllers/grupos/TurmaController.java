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
import com.biopark.cpa.dto.grupos.TurmaDTO;
import com.biopark.cpa.entities.grupos.Turma;
import com.biopark.cpa.form.grupos.TurmaModel;
import com.biopark.cpa.services.grupos.TurmaService;
import com.biopark.cpa.services.utils.CsvParserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/turma")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CPA')")
public class TurmaController {
    private final CsvParserService csvParserService;
    private final TurmaService turmaService;

    @PostMapping
    public ResponseEntity<CadastroDTO> cadastrarTurmas(@RequestParam("file") MultipartFile file, @RequestParam("update") Boolean update) throws IOException{
        List<Turma> turmas = csvParserService.parseCsv(file, Turma.class);
        CadastroDTO cadastroDTO = turmaService.cadastrarTurma(turmas, update);
        return ResponseEntity.status(cadastroDTO.getStatus()).body(cadastroDTO);
    }

    @GetMapping
    public ResponseEntity<List<TurmaDTO>>buscarTodasTurmas() {
        List<TurmaDTO> turmas = turmaService.buscarTodasTurmasDTO();
        return ResponseEntity.status(HttpStatus.OK).body(turmas); 
    }

    @GetMapping("/codigo/{codTurma}")
    public ResponseEntity<TurmaDTO> buscarCodTurma(@PathVariable("codTurma") String codTurma) {
        TurmaDTO turma = turmaService.buscarPorCodigoDTO(codTurma);
        return ResponseEntity.status(HttpStatus.OK).body(turma);
    }

    @GetMapping("/codigo_curso/{codCurso}")
    public ResponseEntity<List<TurmaDTO>> buscarCodCursoTurma(@PathVariable("codCurso") String codCurso) {
        List<TurmaDTO> turmas = turmaService.buscarPorCodigoCursoDTO(codCurso);
        return ResponseEntity.status(HttpStatus.OK).body(turmas);
    }
    
    @PutMapping
    public ResponseEntity<GenericDTO> editarTurma (@RequestBody TurmaModel turma) {
        GenericDTO response = turmaService.editarTurma(turma);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirTurma(@PathVariable("id") Long id) {
        GenericDTO response = turmaService.excluirTurma(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}