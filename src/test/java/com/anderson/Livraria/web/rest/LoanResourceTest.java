package com.anderson.Livraria.web.rest;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.web.dto.LoanDto;
import com.anderson.Livraria.web.dto.LoanFilterDto;
import com.anderson.Livraria.web.dto.ReturnedLoanDto;
import com.anderson.Livraria.web.rest.errors.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static com.anderson.Livraria.service.LoanServiceTestImpl.createLoan;
import static java.time.LocalDate.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanResource.class)
@AutoConfigureMockMvc
public class LoanResourceTest {

    static String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void createLoanTest() throws Exception{


        Book book = Book.builder()
                .id(1L)
                .isbn("123")
                .build();

        LoanDto dto = LoanDto.builder()
                .isbn("123")
                .customer("Joao")
                .email("customer@email.com")
                .build();

        Loan loan = Loan.builder()
                .id(1L)
                .customer("Joao")
                .book(book)
                .loanDate(now())
                .build();


        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));


        given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));


    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de ISBN inexistente")
    public void invalidIsbnCreateLoanTest() throws Exception{

        LoanDto dto = LoanDto.builder()
                .isbn("123")
                .customer("Joao")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer emprestimo de um livro emprestado")
    public void loanedBookErrorOnCreateLoanTest() throws Exception{

        Book book = Book.builder()
                .id(1L)
                .isbn("123")
                .build();


        LoanDto dto = LoanDto.builder()
                .isbn("123")
                .customer("Joao")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));


        given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Book already loaned"));


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }


    @Test
    @DisplayName("deve retornar um livro")
    public void returnBookTest() throws Exception{

        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();
        Loan loan = Loan.builder().id(1L).build();

        given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(returnedLoanDto);

        mockMvc.perform(
                patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect( status().isOk());

        verify(loanService, times(1)).update(loan);
    }

    @Test
    @DisplayName("deve retornar 404 quando tentar devolver um livro inexistente")
    public void returnInexistentBookTest() throws Exception{

        ReturnedLoanDto returnedLoanDto = ReturnedLoanDto.builder().returned(true).build();


        given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(returnedLoanDto);

        mockMvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect( status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar emprestimos")
    public void findLoansTest() throws Exception{

        //Cenario
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);
        Book book = Book.builder().id(1L).isbn("321").build();
        loan.setBook(book);



        BDDMockito.given(loanService.findWithParam(Mockito.any(LoanFilterDto.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 10), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10",
                book.getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));


    }







}

