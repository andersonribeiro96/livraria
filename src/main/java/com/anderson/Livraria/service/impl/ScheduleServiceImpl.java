package com.anderson.Livraria.service.impl;

import com.anderson.Livraria.domain.Loan;
import com.anderson.Livraria.service.EmailService;
import com.anderson.Livraria.service.LoanService;
import com.anderson.Livraria.service.ScheduleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateloans.message}")
    private String mensagem;


    private final LoanService loanService;
    private final EmailService emailService;

    public ScheduleServiceImpl(LoanService loanService, EmailService emailService) {
        this.loanService = loanService;
        this.emailService = emailService;
    }

    @Override
    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailTolateLoans() {
        List<Loan> allLateLoans = loanService.getAllLateLoans();
        List<String> mailList = allLateLoans.stream()
                .map(Loan::getCustomerEmail).collect(Collectors.toList());


        emailService.sendMails(mensagem, mailList);

    }
}
