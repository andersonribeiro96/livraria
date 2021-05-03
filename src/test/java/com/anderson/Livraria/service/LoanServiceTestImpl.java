package com.anderson.Livraria.service;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.repository.LoanRepository;
import com.anderson.Livraria.service.impl.LoanServiceImpl;
import com.anderson.Livraria.web.dto.LoanFilterDto;
import com.anderson.Livraria.web.rest.errors.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTestImpl implements LoanServiceTest {


    LoanService loanService;

    @MockBean
    LoanRepository loanRespository;

    @BeforeEach
    public void setUp() {
        this.loanService = new LoanServiceImpl(loanRespository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo de livro")
    public void saveLoanTest() {
        Book book = Book.builder().id(1L).build();

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Joao")
                .loanDate(now())
                .build();

        Loan savedLoan = Loan.builder()
                .id(1L)
                .book(book)
                .loanDate(now())
                .customer("Joao")
                .build();

        when(loanRespository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(loanRespository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = loanService.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }


    @Test
    @DisplayName("Deve lancar erro de negocio ao salvar livro ja esprestado")
    public void loanedBookSaveTest() {
        Book book = Book.builder().id(1L).build();

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Joao")
                .loanDate(now())
                .build();


        when(loanRespository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> loanService.save(savingLoan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");


        verify(loanRespository, never()).save(savingLoan);
    }

    @Test
    @DisplayName("deve obter as informacoes de um emprestimo pelo ID")
    public void getLoanDetaisTest() {

        Long id = 1L;

        //cenario
        Loan loan = createLoan();
        loan.setId(id);

        when(loanRespository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> resultLoan = loanService.getById(id);

        assertThat(resultLoan.isPresent()).isTrue();
        assertThat(resultLoan.get().getId()).isEqualTo(id);
        assertThat(resultLoan.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(resultLoan.get().getBook()).isEqualTo(loan.getBook());
        assertThat(resultLoan.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(loanRespository, times(1)).findById(id);

    }

    @Test
    @DisplayName("deve atualizar um emprestimo")
    public void updateLoanTest(){

        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);
        loan.setReturned(true);

        when(loanRespository.save(loan)).thenReturn(loan);

        Loan updatedLoan = loanService.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();

        verify(loanRespository, times(1)).save(loan);

    }

    @Test
    @DisplayName("Deve filtrar emprestimos pelas propiedades")
    public void findLoanTest() {

        //Cenario
        LoanFilterDto loanFilterDto = LoanFilterDto.builder().customer("Joao").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1L);

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Loan> loanList = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<Loan>(loanList, pageRequest, loanList.size());

        when(loanRespository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(page);


        //Execução
        Page<Loan> resultPage = loanService.findWithParam(loanFilterDto, pageRequest);


        //Verificação
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent()).isEqualTo(loanList);
        assertThat(resultPage.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(resultPage.getPageable().getPageSize()).isEqualTo(10);

    }



    public static Loan createLoan() {
        Book book = Book.builder().id(1L).build();

        return Loan.builder()
                .book(book)
                .customer("Joao")
                .loanDate(now())
                .build();
    }


}
