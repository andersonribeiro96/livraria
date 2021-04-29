package com.anderson.Livraria.web.rest;

import com.anderson.Livraria.web.dto.BookDTO;
import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.service.BookService;
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

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> get(@PathVariable Long id){
        Book book = bookService.getById(id);
        BookDTO dto = modelMapper.map(book, BookDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BookDTO> delete(@PathVariable Long id){
        BookDTO bookDTO = modelMapper.map(bookService.delete(id), BookDTO.class);
        return new ResponseEntity<>(bookDTO, HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> update(@PathVariable Long id, @RequestBody BookDTO bookDTO){
        Book book = modelMapper.map(bookDTO, Book.class);
        BookDTO dto = modelMapper.map(bookService.update(id, book), BookDTO.class);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<BookDTO> findWithParam(BookDTO bookDTO, Pageable pageableRequest){

        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book>  result =  bookService.findWithParam(filter, pageableRequest);
        List<BookDTO> listBookDto = result.getContent()
                .stream()
                .map( entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(listBookDto, pageableRequest, result.getTotalElements());

    }


}
