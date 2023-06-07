package com.biopark.cpa.services.grupos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.biopark.cpa.dto.GenericDTO;
import com.biopark.cpa.dto.cadastroCsv.CadastroDTO;
import com.biopark.cpa.dto.cadastroCsv.ErroValidation;
import com.biopark.cpa.dto.cadastroCsv.ValidationModel;
import com.biopark.cpa.entities.grupos.Desafio;
import com.biopark.cpa.entities.grupos.Turma;
import com.biopark.cpa.form.grupos.DesafioTurmaModel;
import com.biopark.cpa.repository.grupo.DesafioRepository;
import com.biopark.cpa.repository.grupo.TurmaRepository;
import com.biopark.cpa.services.utils.CsvParserService;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DesafioService {
    private final CsvParserService csvParserService;
    private final DesafioRepository desafioRepository;
    private final TurmaRepository turmaRepository;
    private final Validator validator;

    @Transactional
    public CadastroDTO cadastrarDesafio(List<Desafio> desafios) {
        List<ErroValidation> errors = csvParserService.validaEntrada(desafios);
        List<ErroValidation> warnings = new ArrayList<>();

        if (!errors.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.BAD_REQUEST).erros(errors).warnings(warnings).build();
        }

        ValidationModel<Desafio> model = checarDuplicatas(desafios);
        List<ErroValidation> duplicatas = model.getErrors();
        warnings = model.getWarnings();

        desafios = model.getObjects();

        if (!duplicatas.isEmpty()) {
            return CadastroDTO.builder().status(HttpStatus.CONFLICT).erros(duplicatas).warnings(warnings).build();
        }

        desafioRepository.saveAll(desafios);
        return CadastroDTO.builder().status(HttpStatus.OK).erros(errors).warnings(warnings).build();
    }

    private ValidationModel<Desafio> checarDuplicatas(List<Desafio> desafios) {
        List<ErroValidation> erroValidations = new ArrayList<>();
        List<ErroValidation> warnings = new ArrayList<>();
        List<Desafio> unicos = new ArrayList<>();

        Map<String, Integer> uniqueCod = new HashMap<String, Integer>();

        int linha = 0;
        for (Desafio desafio : desafios) {
            linha++;
            if (!uniqueCod.containsKey(desafio.getNomeDesafio())) {
                uniqueCod.put(desafio.getNomeDesafio(), linha);
                unicos.add(desafio);
            } else {
                warnings.add(ErroValidation.builder()
                        .linha(linha)
                        .mensagem("Esta linha foi ignorada pois o código já existe na linha: "
                                + uniqueCod.get(desafio.getNomeDesafio()))
                        .build());
                continue;
            }

            if (desafioRepository.findByNomeDesafio(desafio.getNomeDesafio().toLowerCase()).isPresent()) {
                erroValidations
                        .add(ErroValidation.builder().linha(linha).mensagem("Desafio já cadastrado").build());
            }
        }

        return ValidationModel.<Desafio>builder().errors(erroValidations).warnings(warnings).objects(unicos)
                .build();
    }

    public GenericDTO associarDesafioTurma(DesafioTurmaModel desafioTurmaModel) {
        Set<ConstraintViolation<DesafioTurmaModel>> violacoes = validator.validate(desafioTurmaModel);

        if (!violacoes.isEmpty()) {
            String mensagem = "";
            for (ConstraintViolation<DesafioTurmaModel> violacao : violacoes) {
                mensagem += violacao.getMessage() + "; ";
            }
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem(mensagem).build();
        }
        desafioTurmaModel.getDesafioId().removeIf(item -> item == null);
        
        if (desafioTurmaModel.getDesafioId().size()<1) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("não foi informado nenhum desafio").build();
        }

        if (desafioTurmaModel.getTurmaId() == 0) {
            return GenericDTO.builder().status(HttpStatus.BAD_REQUEST).mensagem("não foi informado nenhuma turma").build();
        }

        var turmaDB = turmaRepository.findById(Long.valueOf(desafioTurmaModel.getTurmaId()));
        if (!turmaDB.isPresent()) {
            return GenericDTO.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .mensagem("Turma " + desafioTurmaModel.getTurmaId() + " não encontrado")
                    .build();
        }

        Turma turma = turmaDB.get();
        List<Desafio> desafios = new ArrayList<>();

        for (int id : desafioTurmaModel.getDesafioId()) {
            var desafio = desafioRepository.findById(Long.valueOf(id));
            if (!desafio.isPresent()) {
                return GenericDTO.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .mensagem("Desafio " + id + " não encontrado")
                        .build();
            }

            desafios.add(desafio.get());
        }

        if (turma.getDesafios() == null) {
            turma.setDesafios(desafios);
        } else {
            desafios.removeAll(turma.getDesafios());
            desafios.addAll(turma.getDesafios());
        }



        turma.setCodCurso(turma.getCurso().getCodCurso());
        turma.setDesafios(desafios);
        turmaRepository.save(turma);
        return GenericDTO.builder().status(HttpStatus.OK).mensagem("Disciplinas associadas com sucesso").build();
    }

    public Desafio buscarPorId(Long id) {
        var optionalDesafio = desafioRepository.findById(id);

        if (optionalDesafio.isPresent()) {
            return optionalDesafio.get();
        } else {
            throw new RuntimeException("Desafio não encontrado!");
        }
    }

    public List<Desafio> buscarTodosDesafios() {
        List<Desafio> desafios = desafioRepository.findAll();
        if (desafios.isEmpty()) {
            throw new RuntimeException("Não há desafios cadastrados!");
        }
        return desafios;
    }

    // Editar Desafio
    public GenericDTO editarDesafio(Desafio desafioRequest) {
        try {
            Desafio desafio = buscarPorId(desafioRequest.getId());
            desafio.setNomeDesafio(desafioRequest.getNomeDesafio());
            desafioRepository.save(desafio);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("Desafio " + desafioRequest.getId() + " editado com sucesso").build();
        } catch (Exception e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem(e.getMessage()).build();
        }
    }

    // Excluir Desafio
    public GenericDTO excluirDesafio(Long id) {
        // Long id = Long.valueOf(idInt);
        try {
            // Desafio desafio = buscarPorCodigo(id);
            var desafioDB = desafioRepository.findById(id);
            if (!desafioDB.isPresent()) {
                return GenericDTO.builder().status(HttpStatus.NOT_FOUND).mensagem("desafio não encontrada").build();
            }
            Desafio desafio = desafioDB.get();
            desafioRepository.delete(desafio);
            return GenericDTO.builder().status(HttpStatus.OK)
                    .mensagem("desafio " + desafio.getNomeDesafio() + " excluídO com sucesso")
                    .build();
        } catch (EmptyResultDataAccessException e) {
            return GenericDTO.builder().status(HttpStatus.NOT_FOUND)
                    .mensagem("desafio " + id + " não encontrado")
                    .build();
        }
    }
}
