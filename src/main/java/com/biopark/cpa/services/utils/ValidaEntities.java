package com.biopark.cpa.services.utils;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.biopark.cpa.exceptions.InvalidForms;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidaEntities {
    private final Validator validator;

    public <T> void validaEntrada(T data){    
        Set<ConstraintViolation<T>> violacoes = validator.validate(data);

        String mensagem = "";
        if (!violacoes.isEmpty()) {
            for (ConstraintViolation<T> violacao : violacoes) {
                mensagem += violacao.getMessage()+"; ";
            }
        }
        
        if (!mensagem.equals("")) {
            throw new InvalidForms(mensagem);
        }
    }
}