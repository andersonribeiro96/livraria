package com.anderson.Livraria.repository;


import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static com.anderson.Livraria.repository.BookRepositoryTest.bookValid;
import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {


    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe emprestimo nao devolvido para o livro")
    public void existsByBookAndNotReturnedTest(){

        Loan loan = createAndPersistLoan(now());

        Book book = loan.getBook();

        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar emprestimo pelo isbn do livro ou customer")
    public void findByBookIsbnOrCustomerTest(){

        Loan loan = createAndPersistLoan(now());

        Page<Loan> result = loanRepository.findByBookIsbnOrCustomer("123", "Joao", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);

    }

    @Test
    @DisplayName("Deve obter emprestimos cuja data emprestimo for menor ou igua a tres dias atras e nao retornado")
    public void findByLoansDateLessThanAndNotReturnedTest(){
        Loan loan = createAndPersistLoan(now().minusDays(5));
        List<Loan> result = loanRepository.findByLoansDateLessThanAndNotReturned(now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);

    }

    @Test
    @DisplayName("Deve retornar vazio quando nao houver emprestimos atrasados")
    public void notFindByLoansDateLessThanAndNotReturnedTest(){
        Loan loan = createAndPersistLoan(now());
        List<Loan> result = loanRepository.findByLoansDateLessThanAndNotReturned(now().minusDays(4));

        assertThat(result).isEmpty();

    }


    public Loan createAndPersistLoan(LocalDate loanDate){
        Book book = bookValid();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Joao").loanDate(loanDate).build();
        entityManager.persist(loan);

        return loan;
    }


}