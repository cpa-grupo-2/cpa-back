
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
import com.biopark.cpa.dto.grupos.CursoDTO;
import com.biopark.cpa.entities.grupos.Curso;
import com.biopark.cpa.form.grupos.CursoModel;
import com.biopark.cpa.services.grupos.CursoService;
import com.biopark.cpa.services.utils.CsvParserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/curso")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CPA')")
public class CursoController {
    private final CursoService cursoService;
    private final CsvParserService csvParserService;

    @PostMapping
    public ResponseEntity<CadastroDTO> cadastrarCurso(@RequestParam("file") MultipartFile file, @RequestParam(name = "update") Boolean update) throws IOException{
        List<Curso> cursos = csvParserService.parseCsv(file, Curso.class);
        CadastroDTO cadastroDTO = cursoService.cadastrarCurso(cursos, update);
        return ResponseEntity.status(cadastroDTO.getStatus()).body(cadastroDTO);
    }

    @GetMapping
    public ResponseEntity<List<CursoDTO>> buscarTodosCursos() {
        List<CursoDTO> cursos = cursoService.buscarTodosCursosDTO();
        return ResponseEntity.status(HttpStatus.OK).body(cursos);
    }

    @GetMapping("/codigo/{codCurso}")
    public ResponseEntity<CursoDTO> buscarCodigoCurso(@PathVariable("codCurso") String codigoCurso) {
        CursoDTO curso = cursoService.buscarPorCodigoDTO(codigoCurso);
        return ResponseEntity.status(HttpStatus.OK).body(curso);
    }

    @GetMapping("/codigo_instituicao/{codInstituicao}")
    public ResponseEntity<List<CursoDTO>> buscarCursoPorInstituicao(@PathVariable("codInstituicao") String codInstituicao){
        List<CursoDTO> cursos = cursoService.buscarPorInstituicao(codInstituicao);
        return ResponseEntity.status(HttpStatus.OK).body(cursos);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarCurso(@RequestBody CursoModel curso) {
        GenericDTO response = cursoService.editarCurso(curso);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirCurso(@PathVariable("id") Long id) {
        GenericDTO response = cursoService.excluirCurso(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}