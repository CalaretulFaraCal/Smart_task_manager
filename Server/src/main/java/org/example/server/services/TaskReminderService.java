package org.example.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TaskReminderService {

    private final JavaMailSender mailSender;

    @Autowired
    public TaskReminderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async("taskExecutor") // Use the custom thread pool
    public void sendNotificationEmail(String email, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
            System.out.println("Notification sent to: " + email);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + email + ": " + e.getMessage());
        }
    }
}
