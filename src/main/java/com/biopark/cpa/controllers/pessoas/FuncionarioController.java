package com.biopark.cpa.controllers.pessoas;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.entities.pessoas.Funcionario;
import com.biopark.cpa.form.cadastroCsv.FuncionarioModel;
import com.biopark.cpa.repository.pessoas.FuncionarioRepository;
import com.biopark.cpa.services.pessoas.FuncionarioService;
import com.biopark.cpa.services.utils.CsvParserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/funcionario")
public class FuncionarioController {
    private final CsvParserService csvParserService;
    private final FuncionarioService funcionarioService;
    private final FuncionarioRepository funcionarioRepository;

    @PostMapping
    @PreAuthorize("hasRole('CPA')")
    public ResponseEntity<CadastroDTO> cadastrarFuncionario(@RequestParam("file") MultipartFile file,
            @RequestParam("update") boolean update) throws IOException {
        List<FuncionarioModel> funcionarios = csvParserService.parseCsv(file, FuncionarioModel.class);
        CadastroDTO cadastroDTO = funcionarioService.cadastrarFuncionario(funcionarios, update);
        return ResponseEntity.status(cadastroDTO.getStatus()).body(cadastroDTO);
    }

    @GetMapping
    public ResponseEntity<Funcionario>buscarIdFuncionario( @RequestParam(name = "idFuncionario") Long idFuncionario) {
        Funcionario funcionario = funcionarioService.buscarPorID(idFuncionario);
        return ResponseEntity.status(HttpStatus.OK).body(funcionario);
    }

    @GetMapping
    public ResponseEntity<Funcionario> buscarCrachaFuncionario( @RequestParam(name = "crachaFuncionario") String crachaFuncionario) {
        Funcionario funcionario = funcionarioService.buscarPorCracha(crachaFuncionario);
        return ResponseEntity.status(HttpStatus.OK).body(funcionario);
    }

    @GetMapping("/funcionarios")
    public ResponseEntity<List<Funcionario>> buscarTodosFuncionarios() {
        var funcionarios = funcionarioRepository.findAll();
        if (funcionarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(funcionarios);
        }
        return ResponseEntity.status(HttpStatus.OK).body(funcionarios);
    }

    @PutMapping
    public ResponseEntity<GenericDTO> editarFuncionario(@RequestBody Funcionario funcionario) {
        GenericDTO response = funcionarioService.editarFuncionario(funcionario);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @DeleteMapping
    public ResponseEntity<GenericDTO> excluirFuncionario(@RequestParam("id") int idRequest) {
        Long id = Long.valueOf(idRequest);
        GenericDTO response = funcionarioService.excluirFuncionario(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

}
