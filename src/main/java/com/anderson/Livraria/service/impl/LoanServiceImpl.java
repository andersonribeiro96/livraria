package com.anderson.Livraria.service.impl;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.service.LoanService;

public class LoanServiceImpl implements LoanService {

    private final BookService bookService;

    public LoanServiceImpl(BookService bookService) {
        this.bookService = bookService;
    }


    @Override
    public Loan save(Loan loan) {
        return null;
    }
}
