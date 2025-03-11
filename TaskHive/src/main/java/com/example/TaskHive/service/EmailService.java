package com.example.TaskHive.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender)
    {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendEmailToUser(String to,String subject,String text)
    {
        MimeMessage message=javaMailSender.createMimeMessage();
        try
        {
            MimeMessageHelper messageHelper=new MimeMessageHelper(message,true);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(text,true);

            javaMailSender.send(message);
        }
        catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }

    }
}
