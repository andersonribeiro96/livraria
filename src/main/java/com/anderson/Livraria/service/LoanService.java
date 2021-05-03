package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.web.dto.LoanFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface LoanService {

    Loan save(Loan loan);
    Optional<Loan> getById(Long id);
    Loan update(Loan loan);
    Page<Loan> findWithParam(LoanFilterDto any, Pageable pageable);
    Page<Loan> getLoansByBook(Book book, Pageable pageable);
    List<Loan> getAllLateLoans();

}
