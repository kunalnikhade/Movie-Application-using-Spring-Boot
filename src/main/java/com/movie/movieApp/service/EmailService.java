package com.movie.movieApp.service;

import com.movie.movieApp.dto.MailBody;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    private final JavaMailSender javaMailSender;

    public EmailService(final JavaMailSender javaMailSender)
    {
        this.javaMailSender = javaMailSender;
    }

    @Transactional
    public void simpleMessageSender(final MailBody mailBody)
    {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(mailBody.to());
        message.setFrom("Your Email");
        message.setSubject(mailBody.subject());
        message.setText(mailBody.text());

        javaMailSender.send(message);
    }
}
