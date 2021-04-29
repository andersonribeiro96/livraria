package com.anderson.Livraria.web.rest;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.web.dto.LoanDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static java.time.LocalDate.now;
import static org.mockito.BDDMockito.given;
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





}

