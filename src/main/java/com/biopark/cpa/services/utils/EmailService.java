package com.biopark.cpa.services.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public void sendEmail(String template, String email) throws MessagingException{
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(emailFrom));
        message.setRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("Redefinição de senha");
        message.setContent(template, "text/html");
        mailSender.send(message);
    }

    @Async
    public void montaEmail(String token, String email, String nome) throws IOException, MessagingException{
        byte[] bytes = Files.readAllBytes(Paths.get("src/main/resources/view/templateEmail.html"));
        String htmlTemplate = new String(bytes, StandardCharsets.UTF_8);
        htmlTemplate = htmlTemplate.replace("${name}", nome);
        htmlTemplate = htmlTemplate.replace("${linkRedefinirSenha}", "http://localhost:3000/resetar-senha?token=" + token);

        sendEmail(htmlTemplate, email);
    }
}
