package com.anderson.Livraria.service.impl;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.repository.BookRepository;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.web.rest.errors.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    static String ISBN_CADASTRADO = "Isbn ja cadastrado";

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if(bookRepository.existsByIsbn(book.getIsbn())){
            throw new BusinessException(ISBN_CADASTRADO);
        }
        return bookRepository.save(book);
    }
}
