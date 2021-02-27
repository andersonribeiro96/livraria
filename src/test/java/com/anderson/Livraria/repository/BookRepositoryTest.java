package com.anderson.Livraria.repository;

import com.anderson.Livraria.domain.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    static String ISBN = "123";

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existe um livro na base com o isbn informado")
    public void returnTrueWhenIsbnExists(){
        //Cenario
        Book book = bookValid();
        entityManager.persist(book);

        //Execução
        boolean exists = bookRepository.existsByIsbn(ISBN);

        //Verificação
        assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve retornar falso quando não existe um livro na base com o isbn informado")
    public void returnFalseWhenIsbnDoesntExist(){
        //Cenario
        Book book = bookValid();

        //Execução
        boolean exists = bookRepository.existsByIsbn(ISBN);

        //Verificação
        assertThat(exists).isFalse();

    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void findByIdTest(){

        //Cenario
        Book book = bookValid();
        entityManager.persist(book);

        //Execução
        Optional<Book> foundBook = bookRepository.findById(book.getId());

        //Verificação
        assertThat(foundBook.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Deve retorna void ao obter um livro por Id que não exista na base de dados")
    public void NotFoundBookfindByIdTest(){

        //Cenario
        Long id = 1L;

        //Execução
        Optional<Book> notFoundBook = bookRepository.findById(id);

        //Verificação
        assertThat(notFoundBook.isPresent()).isFalse();

    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){

        //Cenario
        Book book = bookValid();

        //Execução
        Book savedBook = entityManager.persist(book);

        //Verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Deve deletar o livro")
    public void deleteBookTest(){

        //Cenario
        Book book = bookValid();
        entityManager.persist(book);

        Book returnBook = entityManager.find(Book.class, book.getId());

        bookRepository.deleteById(returnBook.getId());

        Book deletedBook = entityManager.find(Book.class, book.getId());

        assertThat(deletedBook).isNull();


    }

    private Book bookValid() {
        return Book.builder()
                .isbn(ISBN)
                .author("José Silveira Barbosa")
                .title("Tecnologias e suas ramificações")
                .build();
    }


}
