package com.biopark.cpa.controllers.pessoas;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.form.cadastroCsv.ProfessorModelCsv;
import com.biopark.cpa.services.pessoas.ProfessorService;
import com.biopark.cpa.services.utils.CsvParserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/professor")
@PreAuthorize("hasRole('CPA')")
public class ProfessorController {

    private final CsvParserService csvParserService;
    private final ProfessorService professorService;

    @PostMapping
    public ResponseEntity<CadastroDTO> cadastrarProfessor(@RequestParam("file") MultipartFile file,
            @RequestParam("update") Boolean update) throws IOException {
        List<ProfessorModelCsv> professores = csvParserService.parseCsv(file, ProfessorModelCsv.class);
        CadastroDTO cadastroDTO = professorService.cadastrarProfessor(professores, update);
        return ResponseEntity.status(cadastroDTO.getStatus()).body(cadastroDTO);
    }

    // @GetMapping
    // public ResponseEntity<List<Professor>> buscarTodosProfessores() {
    //     var professores = professorRepository.findAll();
    //     if (professores.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(professores);
    //     }
    //     return ResponseEntity.status(HttpStatus.OK).body(professores);
    // }

    // @GetMapping
    // public ResponseEntity<Optional<Professor>> buscarIdProfessor(
    //         @RequestParam(name = "idProfessor") Long idProfessor) {
    //     var professor = professorRepository.findById(idProfessor);
    //     if (professor == null) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(professor);
    //     }
    //     return ResponseEntity.status(HttpStatus.OK).body(professor);
    // }

    // @GetMapping("/buscarCracha")
    // public ResponseEntity<Optional<Professor>> buscarCrachaProfessor(
    //         @RequestParam(name = "crachaProfessor") String crachaProfessor) {
    //     var professor = professorRepository.findByCracha(crachaProfessor);
    //     if (professor == null) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(professor);
    //     }
    //     return ResponseEntity.status(HttpStatus.OK).body(professor);
    // }



    // @PutMapping
    // public ResponseEntity<GenericDTO> editarProfessor(@RequestBody Professor professor) {
    //     GenericDTO response = professorService.editarProfessor(professor);
    //     return ResponseEntity.status(response.getStatus()).body(response);
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirProfessor(@PathVariable("id") Long id) {
        GenericDTO response = professorService.excluirProfessor(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
