package com.isep.appli.services;

import com.isep.appli.dbModels.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private final String fromEmail = "donotresponddanmachi@gmail.com";
    private final String confirmEmailEndpoint = "http://localhost:8080/confirm?id=";
    private final String confirmEmailSubject = "Confirmer adresse mail";
    private final String confirmEmailBody = "Please click the following link to confirm your email address: ";

    public void sendConfirmationEmail(User user) {
        String confirmationUrl = confirmEmailEndpoint + user.getId();
        String emailContent = confirmEmailBody + confirmationUrl;

        sendEmail(user.getEmail(), emailContent, confirmEmailSubject);
    }

    public void sendEmail(String toEmail, String body, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
