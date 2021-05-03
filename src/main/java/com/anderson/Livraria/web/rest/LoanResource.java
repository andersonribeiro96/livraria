package com.anderson.Livraria.web.rest;


import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.web.dto.BookDto;
import com.anderson.Livraria.web.dto.LoanDto;
import com.anderson.Livraria.web.dto.LoanFilterDto;
import com.anderson.Livraria.web.dto.ReturnedLoanDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;

@RestController
@RequestMapping("/api/loans")
public class LoanResource {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;


    public LoanResource(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
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

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnedBook(@PathVariable Long id, @RequestBody ReturnedLoanDto returnedLoanDto){
        Loan loan = loanService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(returnedLoanDto.getReturned());
        loanService.update(loan);
    }


    @GetMapping
    public Page<LoanDto> find(LoanFilterDto loanFilterDto, Pageable pageRequest){
        Page<Loan> result = loanService.findWithParam(loanFilterDto, pageRequest);
        List<LoanDto> loanDtoList = result
                .getContent()
                .stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDto bookDto = modelMapper.map(book, BookDto.class);
                    LoanDto loanDto = modelMapper.map(entity, LoanDto.class);
                    loanDto.setBookDto(bookDto);
                    return loanDto;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDto>(loanDtoList, pageRequest, result.getTotalElements());
    }


}
