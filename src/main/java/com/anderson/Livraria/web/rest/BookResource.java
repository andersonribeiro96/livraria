package com.anderson.Livraria.web.rest;

import com.anderson.Livraria.web.dto.BookDTO;
import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookResource {

    private final BookService bookService;
    private final ModelMapper modelMapper;



    public BookResource(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }


    @PostMapping
    public ResponseEntity<BookDTO> create(@RequestBody @Valid BookDTO bookDTO){
        Book book = modelMapper.map(bookDTO, Book.class);
        BookDTO dto = modelMapper.map(bookService.save(book), BookDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


}
