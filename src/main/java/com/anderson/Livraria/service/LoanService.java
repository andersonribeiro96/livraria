package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Loan;
import org.springframework.stereotype.Service;


@Service
public interface LoanService {

    Loan save(Loan loan);

}
