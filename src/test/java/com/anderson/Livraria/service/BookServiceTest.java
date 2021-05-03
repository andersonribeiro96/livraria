package com.anderson.Livraria.service;

public interface BookServiceTest {

    void saveBookTest();
    void shouldNotSaveABookWithDuplicateISBN();
    void BookNotFoundGetById();
    void getByIdTest();
    void bookNotFoundById();
    void deleteBookById();
    void deleteBookNotFound();
    void updateBookById();
    void updadteBookNotFound();
    void findWithParamTest();
}
