package com.anderson.Livraria.errors;


import com.anderson.Livraria.web.rest.errors.ApiErrors;
import com.anderson.Livraria.web.rest.errors.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApplicationControllerAdvice {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleValidationExceptions(MethodArgumentNotValidException exception){
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErros(bindingResult);
    }


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErros handleBusinessExceptions(BusinessException exception){
        return new ApiErros(exception);
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleResponseStatusExceptions( ResponseStatusException exception){
        return new ResponseEntity(new ApiErros(exception), exception.getStatus());
    }



}
