package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendConfirmationToCustomer(String customerEmail, String customerName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(customerEmail);
        message.setSubject("QR Patrol - Cảm ơn bạn đã liên hệ");
        message.setText("Chào " + customerName + ",\n\nCảm ơn bạn đã quan tâm đến QR Patrol. Chúng tôi đã nhận được tin nhắn của bạn và sẽ phản hồi trong thời gian sớm nhất.\n\nTrân trọng,\nĐội ngũ QR Patrol");
        mailSender.send(message);
    }

    public void sendNotificationToAdmin(String customerName, String customerEmail, String customerMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(adminEmail); // <-- Bây giờ biến này sẽ có giá trị psaigon179@gmail.com
        message.setSubject("Có tin nhắn liên hệ mới từ: " + customerName);
        message.setText("Bạn có một tin nhắn mới từ trang liên hệ:\n\n" +
                "Tên khách hàng: " + customerName + "\n" +
                "Email: " + customerEmail + "\n" +
                "Nội dung:\n" + customerMessage);
        mailSender.send(message);
    }
}