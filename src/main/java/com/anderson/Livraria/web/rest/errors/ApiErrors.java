package com.anderson.Livraria.web.rest.errors;

import java.util.Arrays;
import java.util.List;

public class ApiErrors {


    private List<String> erros;

    public ApiErrors(List<String> erros){
        this.erros = erros;
    }

    public ApiErrors(String message){
        this.erros = Arrays.asList(message);
    }

}
