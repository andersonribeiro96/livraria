package com.anderson.Livraria.web.rest;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.web.dto.BookDto;
import com.anderson.Livraria.service.BookService;
import com.anderson.Livraria.web.rest.errors.BookNotFoundException;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookResource.class)
@AutoConfigureMockMvc

public class BookResourceTest {

    static String BOOK_API = "/api/books";
    static String ISBN_CADASTRADO = "Isbn ja cadastrado";


    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Autowired
    MockMvc mockMvc;

    private BookDto createNewBookDTO() {
        return BookDto.builder()
                .author("Fracisco Emannuel")
                .title("As aventuras de Pitombas")
                .isbn("00123")
                .build();
    }

    private Book createNewBook(Long id) {
        return Book.builder()
                .id(id)
                .author("Fracisco Emannuel")
                .title("As aventuras de Pitombas")
                .isbn("00123")
                .build();
    }


    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {

        BookDto bookDTO = createNewBookDTO();

        Book savedBook = Book.builder()
                .id(101L)
                .author("Fracisco Emannuel")
                .title("As aventuras de Pitombas")
                .isbn("00123")
                .build();

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(jsonPath("isbn").value(bookDTO.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para a criação do livro")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDto());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);


        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve lançar um error ao tentar cadastrar livro com ISBN existente na base de dados")
    public void createBookWithDuplicateIsbn() throws Exception {

        BookDto bookDTO = createNewBookDTO();

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException(ISBN_CADASTRADO));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0]").value("Isbn ja cadastrado"));
    }


    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetailsTest() throws Exception {
        //Cenario
        Long id = 1L;

        Book book = createNewBook(id);
        BDDMockito.given(bookService.getById(id)).willReturn(book);

        //Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //Verificação
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBookDTO().getTitle()))
                .andExpect(jsonPath("author").value(createNewBookDTO().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBookDTO().getIsbn()));


    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro não existir")
    public void bookNotFoundTest() throws Exception {

        //Cenario
        BDDMockito.given(bookService.getById(anyLong())).willThrow(BookNotFoundException.class);


        //Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);


        //Verificação
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("deve deletar um livro")
    public void deleteBookTest() throws Exception {

        //Cenario
        BDDMockito.given(bookService.delete(anyLong())).willReturn(createNewBook(1L));


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("deve retornar not found quando tentar deletar um livro que não existe")
    public void deleteInesxistentBookTest() throws Exception {

        //Cenario
        BDDMockito.given(bookService.delete(anyLong())).willThrow(BookNotFoundException.class);


        //Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);


        //Verificação
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {

        //Cenario
        Book bookUpdate = Book.builder()
                .title("some title")
                .author("some author")
                .build();

        BDDMockito.given(bookService.update(1L, bookUpdate)).willReturn(bookUpdate);
        String json = new ObjectMapper().writeValueAsString(bookUpdate);

        //Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //Verificação
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("title").value(bookUpdate.getTitle()))
                .andExpect(jsonPath("author").value(bookUpdate.getAuthor()));
    }

    @Test
    @DisplayName("Deve retornar not found quando tentar atualizar um livro que não existe")
    public void updateInesxistentBookTest() throws Exception {

        //Cenario
        BDDMockito.given(bookService.update(anyLong(), Mockito.any(Book.class))).willThrow(BookNotFoundException.class);
        String json = new ObjectMapper().writeValueAsString(createNewBook(1L));

        //Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);


        //Verificação
        mockMvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooksTest() throws Exception{

        //Cenario
        Long id = 1L;
        Book book = createNewBook(id);

        BDDMockito.given(bookService.findWithParam(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(),
                book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));


    }


}
