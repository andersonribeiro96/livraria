package com.anderson.Livraria.web.rest;

import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.web.dto.BookDto;
import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.web.dto.LoanDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/books")
public class BookResource {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final LoanService loanService;



    public BookResource(BookService bookService, ModelMapper modelMapper, LoanService loanService) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
        this.loanService = loanService;
    }


    @PostMapping
    public ResponseEntity<BookDto> create(@RequestBody @Valid BookDto bookDTO){
        Book book = modelMapper.map(bookDTO, Book.class);
        BookDto dto = modelMapper.map(bookService.save(book), BookDto.class);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> get(@PathVariable Long id){
        Book book = bookService.getById(id);
        BookDto dto = modelMapper.map(book, BookDto.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookDto> delete(@PathVariable Long id){
        BookDto bookDTO = modelMapper.map(bookService.delete(id), BookDto.class);
        return new ResponseEntity<>(bookDTO, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDto> update(@PathVariable Long id, @RequestBody BookDto bookDTO){
        Book book = modelMapper.map(bookDTO, Book.class);
        BookDto dto = modelMapper.map(bookService.update(id, book), BookDto.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDto> findWithParam(BookDto bookDTO, Pageable pageableRequest){

        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book>  result =  bookService.findWithParam(filter, pageableRequest);
        List<BookDto> listBookDto = result.getContent()
                .stream()
                .map( entity -> modelMapper.map(entity, BookDto.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDto>(listBookDto, pageableRequest, result.getTotalElements());

    }

    @GetMapping("/{id}/loans")
    public Page<LoanDto> loansByBook(@PathVariable Long id, Pageable pageable){
        Book book = bookService.getById(id);
        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDto> list = result.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDto bookDto = modelMapper.map(loanBook, BookDto.class);
                    LoanDto loanDto = modelMapper.map(loan, LoanDto.class);
                    loanDto.setBookDto(bookDto);
                    return loanDto;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDto>(list, pageable, result.getTotalElements());
    }




}
