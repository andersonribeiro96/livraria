package com.anderson.Livraria.web.rest.errors.handler;

import com.anderson.Livraria.web.rest.errors.BusinessException;
import com.anderson.Livraria.web.rest.errors.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class HandlerCustomizeException extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<ExceptionResponse> handlerIsbnJaCadastrado(Exception ex) {
        String exceptionResponse = (ex.getMessage());
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }



}
