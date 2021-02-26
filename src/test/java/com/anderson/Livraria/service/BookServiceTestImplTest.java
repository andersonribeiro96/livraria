package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.repository.BookRepository;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.service.impl.BookServiceImpl;
import com.anderson.Livraria.web.rest.errors.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTestImplTest implements BookServiceTest{

    static String ISBN_CADASTRADO = "Isbn ja cadastrado";

    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp(){
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar o livro")
    public void saveBookTest(){

        //Cenário
        Book book = createValidBook();
        Mockito.when( bookRepository.existsByIsbn(Mockito.anyString() )).thenReturn(false);

        Book bookRetornado = Book.builder()
                .id(1L)
                .isbn("123")
                .author("José Silveira Barbosa")
                .title("Tecnologias e suas ramificações")
                .build();

        when(bookRepository.save(book)).thenReturn(bookRetornado);

        //Execução
        Book savedBook = bookService.save(book);

        //Verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo("José Silveira Barbosa");
        assertThat(savedBook.getTitle()).isEqualTo("Tecnologias e suas ramificações");
        assertThat(savedBook.getIsbn()).isEqualTo("123");
    }


    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicateISBN(){
        //Cenario
        Book book = createValidBook();
        Mockito.when( bookRepository.existsByIsbn(Mockito.anyString() )).thenReturn(true);

        //Execução
        Throwable throwable = Assertions.catchThrowable( () -> bookService.save(book) );

        //Verificação
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ISBN_CADASTRADO);

        verify(bookRepository, Mockito.never()).save(book);

    }



    private Book createValidBook() {
        return Book.builder()
                .isbn("123")
                .author("José Silveira Barbosa")
                .title("Tecnologias e suas ramificações")
                .build();
    }



}
