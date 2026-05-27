package com.otptgbot.NotificationServices;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class MailDispatcher implements DeliveryContract {

    private final Session mailSession;
    private final String senderEmail;

    public MailDispatcher(
            @Value("${email.username}") String smtpUsername,
            @Value("${email.password}") String smtpPassword,
            @Value("${email.from}") String fromEmail,
            @Value("${mail.smtp.host}") String smtpHost,
            @Value("${mail.smtp.port}") String smtpPort,
            @Value("${mail.smtp.auth}") String smtpAuth,
            @Value("${mail.smtp.starttls.enable}") String starttls) {

        this.senderEmail = fromEmail;

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.host", smtpHost);
        mailProperties.put("mail.smtp.port", smtpPort);
        mailProperties.put("mail.smtp.auth", smtpAuth);
        mailProperties.put("mail.smtp.starttls.enable", starttls);

        boolean authEnabled = Boolean.parseBoolean(smtpAuth);
        this.mailSession = Session.getInstance(mailProperties, authEnabled ? new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        } : null);
    }

    @Override
    public void sendCode(String recipientEmail, String otpCode) {
        try {
            Message emailMessage = new MimeMessage(mailSession);
            emailMessage.setFrom(new InternetAddress(senderEmail));
            emailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            emailMessage.setSubject("Your OTP Code");
            emailMessage.setText("Your verification code is: " + otpCode);
            Transport.send(emailMessage);
            log.info("Email OTP sent to {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", recipientEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}