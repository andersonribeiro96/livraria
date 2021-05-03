package com.anderson.Livraria.service.impl;

import com.anderson.Livraria.domain.Book;
import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.repository.LoanRepository;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.web.dto.LoanFilterDto;
import com.anderson.Livraria.web.rest.errors.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }


    @Override
    public Loan save(Loan loan) {
        if(loanRepository.existsByBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Book already loaned");
        }
        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return loanRepository.save(loan);
    }

    @Override
    public Page<Loan> findWithParam(LoanFilterDto filterDto, Pageable pageable) {
        return loanRepository.findByBookIsbnOrCustomer(filterDto.getIsbn(), filterDto.getCustomer(), pageable);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return loanRepository.findByBook(book, pageable);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);

        return loanRepository.findByLoansDateLessThanAndNotReturned(threeDaysAgo);
    }
}
