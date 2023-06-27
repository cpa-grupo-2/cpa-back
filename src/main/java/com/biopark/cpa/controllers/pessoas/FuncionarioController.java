package com.biopark.cpa.controllers.pessoas;

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
import com.biopark.cpa.dto.pessoas.FuncionarioDTO;
import com.biopark.cpa.form.cadastroCsv.FuncionarioModelCsv;
import com.biopark.cpa.form.pessoas.FuncionarioModel;
import com.biopark.cpa.services.pessoas.FuncionarioService;
import com.biopark.cpa.services.utils.CsvParserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/funcionario")
@PreAuthorize("hasRole('CPA')")
public class FuncionarioController {
    private final CsvParserService csvParserService;
    private final FuncionarioService funcionarioService;

    @PostMapping
    public ResponseEntity<CadastroDTO> cadastrarFuncionario(@RequestParam("file") MultipartFile file,
            @RequestParam("update") boolean update) throws IOException {
        List<FuncionarioModelCsv> funcionarios = csvParserService.parseCsv(file, FuncionarioModelCsv.class);
        CadastroDTO cadastroDTO = funcionarioService.cadastrarFuncionario(funcionarios, update);
        return ResponseEntity.status(cadastroDTO.getStatus()).body(cadastroDTO);
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioDTO>> buscarTodosFuncionarios() {
        List<FuncionarioDTO> funcionarios = funcionarioService.buscarTodosFuncionariosDTO();
        return ResponseEntity.status(HttpStatus.OK).body(funcionarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioDTO> buscarIdFuncionario(@PathVariable(name = "id") Long id) {
        FuncionarioDTO funcionario = funcionarioService.buscarPorIdDTO(id);
        return ResponseEntity.status(HttpStatus.OK).body(funcionario);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarFuncionario(@RequestBody FuncionarioModel funcionario) {
        GenericDTO response = funcionarioService.editaFuncionario(funcionario);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericDTO> excluirFuncionario(@PathVariable("id") Long id) {
        GenericDTO response = funcionarioService.excluirFuncionario(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
