package com.biopark.cpa.exceptions;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ MissingServletRequestPartException.class, MissingServletRequestParameterException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionModel missingParams() {
        return ExceptionModel.builder()
                .status(HttpStatus.BAD_REQUEST)
                .mensagem("Um ou mais parâmetros obrigatórios não foram informados")
                .build();
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionModel invalidParams() {
        return ExceptionModel.builder()
                .status(HttpStatus.BAD_REQUEST)
                .mensagem("Tipo de parâmetro inválido")
                .build();
    }

    @ExceptionHandler({ MalformedJwtException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionModel invalidToken() {
        return ExceptionModel.builder().status(HttpStatus.BAD_REQUEST).mensagem("Token mal formatado").build();
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ExceptionModel methodNotAllowed() {
        return ExceptionModel.builder().status(HttpStatus.METHOD_NOT_ALLOWED).mensagem("Método não permitido").build();
    }

    @ExceptionHandler({ IOException.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionModel fileError(IOException e) {
        return ExceptionModel.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).mensagem("tivemos erros lendo arquivos: "+e.getMessage()).build();
    }

    @ExceptionHandler({ExpiredJwtException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ExceptionModel expiredToken(){
        return ExceptionModel.builder().status(HttpStatus.FORBIDDEN).mensagem("token inválido").build();
    }

    @ExceptionHandler({ NoSuchElementException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ExceptionModel ElementNotFound(NoSuchElementException e) {
        return ExceptionModel.builder().status(HttpStatus.NOT_FOUND).mensagem(e.getMessage()).build();
    }

    @ExceptionHandler({ InvalidForms.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionModel invalidForm(InvalidForms e){
        return ExceptionModel.builder().status(HttpStatus.BAD_REQUEST).mensagem(e.getMessage()).build();
    }
}