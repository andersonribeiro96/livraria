package com.anderson.Livraria.service;

import org.springframework.stereotype.Service;

import java.util.List;


public interface EmailService {
    void sendMails(String mensagem, List<String> mailList);

}
