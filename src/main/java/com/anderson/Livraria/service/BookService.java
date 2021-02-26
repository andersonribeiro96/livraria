package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Book;
import org.springframework.stereotype.Service;


public interface BookService {
    Book save(Book book);
}
