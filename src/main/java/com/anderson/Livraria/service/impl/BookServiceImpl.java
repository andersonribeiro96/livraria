package com.anderson.Livraria.service.impl;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.repository.BookRepository;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.web.rest.errors.BookNotFoundException;
import com.anderson.Livraria.web.rest.errors.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    static String ISBN_CADASTRADO = "Isbn ja cadastrado";
    static String BOOK_NOT_FOUND = "Livro não encontrado na base de dados";

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

    @Override
    public Book getById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if(book.isPresent()){
            return book.get();
        }
        throw new BookNotFoundException(BOOK_NOT_FOUND);
    }

    @Override
    public Book delete(Long id) {
        Book bookRetornado = getById(id);
        bookRepository.deleteById(bookRetornado.getId());
        return bookRetornado;
    }

    @Override
    public Book update(Long id, Book book) {
        Book bookRetornado = getById(id);
        bookRetornado.setAuthor(book.getAuthor());
        bookRetornado.setTitle(book.getTitle());
        return bookRepository.save(bookRetornado);
    }
}
