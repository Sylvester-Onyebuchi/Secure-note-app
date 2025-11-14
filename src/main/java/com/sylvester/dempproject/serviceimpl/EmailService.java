package com.sylvester.dempproject.serviceimpl;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;


    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String to,String name, long text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Confirmation of email");
        mailMessage.setText("Dear " + name + ",your verification code is "+text+ "\nEnter the code in next 10 hours to get your account verified.\nYours app team");
        mailMessage.setFrom(sender);
        javaMailSender.send(mailMessage);
    }

    @Async
    public void sendVerifiedEmail(String to, String name) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Account verification success");
        mailMessage.setText("Dear " + name +",Your account has been verified\nYou are free to enjoy our website😁\nYours app team");
        mailMessage.setFrom(sender);
        javaMailSender.send(mailMessage);
    }

    @Async
    public void sendNewVerificationCode(String to, String name, long text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("New verification code");
        mailMessage.setText("Dear "+name+", Your new verification code is "+text+". Verify your account with the code within 10 hours.\nYours app team");
        mailMessage.setFrom(sender);
        javaMailSender.send(mailMessage);
    }

    @Async
    public void sendPasswordResetEmail( String email, String resetUrl,String name) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Password reset");
        mailMessage.setText("Dear "+name+", Use this link sent to you to reset your password\n"+resetUrl+"\nYours app team");
        mailMessage.setFrom(sender);
        javaMailSender.send(mailMessage);
    }

}
