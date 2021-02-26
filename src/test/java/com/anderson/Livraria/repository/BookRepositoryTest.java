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

    private Book bookValid() {
        return Book.builder()
                .isbn(ISBN)
                .author("José Silveira Barbosa")
                .title("Tecnologias e suas ramificações")
                .build();
    }


}
