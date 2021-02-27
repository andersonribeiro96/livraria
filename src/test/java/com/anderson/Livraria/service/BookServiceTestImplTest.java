package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.repository.BookRepository;
import com.anderson.Livraria.service.impl.BookServiceImpl;
import com.anderson.Livraria.web.rest.errors.BookNotFoundException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTestImplTest implements BookServiceTest {

    static String ISBN_CADASTRADO = "Isbn ja cadastrado";
    static String BOOK_NOT_FOUND = "Livro não encontrado na base de dados";

    private BookService bookService;

    @MockBean
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar o livro")
    public void saveBookTest() {

        //Cenário
        Book book = createValidBook();
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);

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
    public void shouldNotSaveABookWithDuplicateISBN() {
        //Cenario
        Book book = createValidBook();
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        //Execução
        Throwable throwable = catchThrowable(() -> bookService.save(book));

        //Verificação
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage(ISBN_CADASTRADO);

        verify(bookRepository, never()).save(book);

    }


    @Test
    @DisplayName("Deve lançar um erro ao tentar procurar um livro que não existe")
    public void BookNotFoundGetById() {

        //Cenario
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Execução
        Throwable throwable = catchThrowable(() -> bookService.getById(id));

        //Verificação
        assertThat(throwable)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage(BOOK_NOT_FOUND);

        verify(bookRepository, times(1)).findById(id);

    }

    @Test
    @DisplayName("deve obter um livro por Id")
    public void getByIdTest() {

        //Cenario
        Long id = 1L;
        Book book = createValidBook();
        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Execução
        Optional<Book> foundBook = bookRepository.findById(id);

        //Verificação
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());

        verify(bookRepository, times(1)).findById(id);

    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por ID que não existe na base")
    public void bookNotFoundById() {

        //Cenario
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Execução
        Optional<Book> book = bookRepository.findById(id);

        //Verificação
        assertThat(book.isPresent()).isFalse();
        verify(bookRepository, times(1)).findById(id);

    }

    @Test
    @DisplayName("Deve deletar um livro por Id")
    public void deleteBookById() {

        //Cenario
        Book book = createValidBook();


        //Execução
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        bookService.delete(book.getId());

        //Verificação
        verify(bookRepository, times(1)).deleteById(book.getId());
    }

    @Test
    @DisplayName("Deve lançar um erro ao tentar deletar livro que não existe na base de dados")
    public void deleteBookNotFound() {

        //Cenario
        Long id = 1L;

        //Execução
        Throwable throwable = catchThrowable(() -> bookService.delete(id));

        //Verificação
        assertThat(throwable)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage(BOOK_NOT_FOUND);

        verify(bookRepository, Mockito.never()).deleteById(id);

    }

    @Test
    @DisplayName("Deve atualizar livro pelo Id")
    public void updateBookById() {

        //Cenario
        Long id = 1L;
        Book book = Book.builder().id(id).build();
        Book updateBook = createValidBook();
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.ofNullable(book));
        Mockito.when(bookRepository.save(book)).thenReturn(updateBook);

        //Execução
        Book returnBook = bookService.update(id, updateBook);

        //Verificação
        assertThat(returnBook.getId()).isEqualTo(updateBook.getId());
        assertThat(returnBook.getTitle()).isEqualTo(updateBook.getTitle());
        assertThat(returnBook.getAuthor()).isEqualTo(updateBook.getAuthor());
        assertThat(returnBook.getIsbn()).isEqualTo(updateBook.getIsbn());

        verify(bookRepository, times(1)).save(book);


    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar livro que não existe na base de dados")
    public void updadteBookNotFound() {

        //Cenario
        Long id = 1L;
        Book book = new Book();

        //Execução
        Throwable throwable = catchThrowable(() -> bookService.update(id, book));

        //Verificação
        assertThat(throwable)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage(BOOK_NOT_FOUND);

        verify(bookRepository, Mockito.never()).save(book);

    }


    private Book createValidBook() {
        return Book.builder()
                .id(1L)
                .isbn("123")
                .author("José Silveira Barbosa")
                .title("Tecnologias e suas ramificações")
                .build();
    }


}
