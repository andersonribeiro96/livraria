package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book save(Book book);
    Book getById(Long id);
    Book delete(Long id);
    Book update(Long id, Book book);
    Page<Book> findWithParam(Book filter, Pageable pageRequest);
    Optional<Book> getBookByIsbn(String s);
}
