package com.anderson.Livraria.web.rest;


import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.web.dto.LoanDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static java.time.LocalDate.now;

@RestController
@RequestMapping("/api/loans")
public class LoanResource {

    private final LoanService loanService;
    private final BookService bookService;


    public LoanResource(LoanService loanService, BookService bookService) {
        this.loanService = loanService;
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDto loanDto){
        Book book = bookService.getBookByIsbn(loanDto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));


        Loan entity = Loan.builder()
                .book(book)
                .customer(loanDto.getCustomer())
                .loanDate(now())
                .build();

        entity = loanService.save(entity);
        return entity.getId();
    }


}
