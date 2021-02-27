package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Book;

public interface BookService {
    Book save(Book book);
    Book getById(Long id);
    Book delete(Long id);
    Book update(Long id, Book book);
}
