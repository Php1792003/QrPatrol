package com.example.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateService templateService;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Value("${spring.mail.from}")
    private String fromEmail;

    // Original method - updated to use HTML template
    public void sendConfirmationToCustomer(String customerEmail, String customerName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(customerEmail);
            helper.setSubject("QR Patrol - Cảm ơn bạn đã liên hệ");

            // Create HTML content using template
            String htmlContent = templateService.createCustomerConfirmationTemplate(
                    customerName, "Liên hệ", "Cảm ơn bạn đã quan tâm đến QR Patrol");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            // Fallback to simple text email if HTML fails
            sendSimpleConfirmationEmail(customerEmail, customerName);
        }
    }

    // Enhanced method with more parameters
    public void sendConfirmationToCustomer(String customerEmail, String customerName, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(customerEmail);
            helper.setSubject(subject);

            String htmlContent = templateService.createCustomerConfirmationTemplate(
                    customerName, subject, message);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send confirmation email", e);
        }
    }

    // Original method - updated to use HTML template
    public void sendNotificationToAdmin(String customerName, String customerEmail, String customerMessage) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(adminEmail);
            helper.setSubject("🔔 Tin nhắn liên hệ mới từ: " + customerName);

            String htmlContent = templateService.createAdminNotificationTemplate(
                    customerName, customerEmail, "", "", "Liên hệ", customerMessage, false);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // Fallback to simple text email if HTML fails
            sendSimpleAdminNotification(customerName, customerEmail, customerMessage);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // Enhanced method with more parameters
    public void sendNotificationToAdmin(String customerName, String customerEmail,
                                        String customerPhone, String company,
                                        String subject, String message, boolean newsletter) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(adminEmail);
            helper.setSubject("🔔 Tin nhắn liên hệ mới từ: " + customerName);

            String htmlContent = templateService.createAdminNotificationTemplate(
                    customerName, customerEmail, customerPhone, company, subject, message, newsletter);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send admin notification", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // New method for trial confirmation
    public void sendTrialConfirmationEmail(String email, String customerName,
                                           String username, String password, String companyName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(email);
            helper.setSubject("🎉 Tài khoản dùng thử QR Patrol đã sẵn sàng!");

            String htmlContent = templateService.createTrialConfirmationTemplate(
                    customerName, username, password, companyName);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            System.out.println("✅ Trial confirmation email sent to: " + email);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("❌ Failed to send trial confirmation email: " + e.getMessage());
            throw new RuntimeException("Failed to send trial confirmation email", e);
        }
    }

    // New method for welcome email
    public void sendWelcomeEmail(String customerEmail, String customerName, String companyName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(customerEmail);
            helper.setSubject("🎉 Chào mừng đến với QR Patrol!");

            String htmlContent = templateService.createWelcomeEmailTemplate(customerName, companyName);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("❌ Failed to send welcome email: " + e.getMessage());
        }
    }

    // New method for account expiry reminder
    public void sendExpiryReminderEmail(String customerEmail, String customerName,
                                        String username, int daysLeft) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(customerEmail);
            helper.setSubject("⏰ Tài khoản QR Patrol sắp hết hạn - " + daysLeft + " ngày");

            String htmlContent = templateService.createExpiryReminderTemplate(
                    customerName, username, daysLeft);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("❌ Failed to send expiry reminder: " + e.getMessage());
        }
    }

    // Fallback methods (simple text emails)
    private void sendSimpleConfirmationEmail(String customerEmail, String customerName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(customerEmail);
            message.setSubject("QR Patrol - Cảm ơn bạn đã liên hệ");
            message.setText("Chào " + customerName + ",\n\nCảm ơn bạn đã quan tâm đến QR Patrol. Chúng tôi đã nhận được tin nhắn của bạn và sẽ phản hồi trong thời gian sớm nhất.\n\nTrân trọng,\nĐội ngũ QR Patrol");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Failed to send simple confirmation email: " + e.getMessage());
        }
    }

    private void sendSimpleAdminNotification(String customerName, String customerEmail, String customerMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("Có tin nhắn liên hệ mới từ: " + customerName);
            message.setText("Bạn có một tin nhắn mới từ trang liên hệ:\n\n" +
                    "Tên khách hàng: " + customerName + "\n" +
                    "Email: " + customerEmail + "\n" +
                    "Nội dung:\n" + customerMessage);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Failed to send simple admin notification: " + e.getMessage());
        }
    }

    // Utility method to test email configuration
    public boolean testEmailConfiguration() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail);
            message.setSubject("QR Patrol - Test Email");
            message.setText("This is a test email to verify email configuration is working correctly.\n\nTimestamp: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Email configuration test failed: " + e.getMessage());
            return false;
        }
    }

    // Utility method to send custom HTML email
    public void sendCustomHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, "QR Patrol System");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send custom HTML email", e);
        }
    }

    // Bulk email method
    public void sendBulkEmail(String[] recipients, String subject, String htmlContent) {
        for (String recipient : recipients) {
            try {
                sendCustomHtmlEmail(recipient, subject, htmlContent);
                Thread.sleep(100); // Small delay to avoid overwhelming SMTP server
            } catch (Exception e) {
                System.err.println("❌ Failed to send email to: " + recipient + " - " + e.getMessage());
            }
        }
    }
}
