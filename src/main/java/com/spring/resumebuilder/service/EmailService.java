package com.spring.resumebuilder.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    public void sendHtmlEmail(String to,String subject,String htmlcontent) throws MessagingException {
        log.info("Inside EmailService: sendHtmlEmail() {}, {},{}", to,subject,htmlcontent);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlcontent,true);
        mailSender.send(message);
        log.info("Email sent successfully to: {}", to);

    }



}
