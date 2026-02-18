package com.uberclocked.api.emailData;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    public void sendToMany(List<String> to, String subject, String body) {
        if (to == null || to.isEmpty()) return;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to.toArray(new String[0]));
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }
}
