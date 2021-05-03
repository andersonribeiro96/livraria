package com.anderson.Livraria.service.impl;

import com.anderson.Livraria.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${application.mail.default-remetente}")
    private String remetente;

    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMails(String mensagem, List<String> mailList) {
        String[] mails = mailList.toArray(new String[mailList.size()]);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetente);
        mailMessage.setSubject("Livro com emprestimo atrasado");
        mailMessage.setText(mensagem);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);
    }
}
