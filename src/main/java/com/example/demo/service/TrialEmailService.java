package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrialEmailService {

    @Autowired
    private EmailTemplateService templateService;

    @Autowired
    private EmailService emailService;

    public void sendTrialConfirmationEmail(String email, String customerName,
                                           String username, String password, String companyName) {
        try {
            String subject = "🎉 Tài khoản dùng thử QR Patrol đã sẵn sàng!";
            String htmlContent = createTrialConfirmationTemplate(
                    customerName, username, password, companyName);

            // Use existing email service infrastructure
            emailService.sendConfirmationToCustomer(email, customerName, subject, htmlContent);

            System.out.println("✅ Trial confirmation email sent to: " + email);

        } catch (Exception e) {
            System.err.println("❌ Failed to send trial confirmation email: " + e.getMessage());
            throw new RuntimeException("Failed to send trial confirmation email", e);
        }
    }

    private String createTrialConfirmationTemplate(String customerName, String username,
                                                   String password, String companyName) {
        return String.format("""
            <div style="max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif;">
                <div style="background: linear-gradient(135deg, #6366F1, #8B5CF6); padding: 30px; text-align: center; color: white;">
                    <h1>🎉 Chào mừng đến với QR Patrol!</h1>
                    <p>Tài khoản dùng thử của bạn đã sẵn sàng</p>
                </div>
                
                <div style="padding: 30px; background: white;">
                    <h2>Chào %s,</h2>
                    <p>Cảm ơn bạn đã đăng ký dùng thử QR Patrol! Tài khoản của <strong>%s</strong> đã được tạo thành công.</p>
                    
                    <div style="background: #f8fafc; border: 2px solid #e2e8f0; border-radius: 8px; padding: 20px; margin: 20px 0;">
                        <h3>🔐 Thông tin đăng nhập:</h3>
                        <p><strong>Username:</strong> <code style="background: #e2e8f0; padding: 4px 8px; border-radius: 4px;">%s</code></p>
                        <p><strong>Password:</strong> <code style="background: #e2e8f0; padding: 4px 8px; border-radius: 4px;">%s</code></p>
                        <p><strong>Thời gian dùng thử:</strong> 30 ngày miễn phí</p>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="https://app.qrpatrol.vn" style="background: #6366F1; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; display: inline-block;">
                            🚀 Đăng nhập ngay
                        </a>
                    </div>
                    
                    <h3>📱 Tải ứng dụng mobile:</h3>
                    <p>
                        <a href="https://apps.apple.com/app/qrpatrol">📱 iOS App Store</a> | 
                        <a href="https://play.google.com/store/apps/qrpatrol">🤖 Google Play</a>
                    </p>
                    
                    <h3>💬 Cần hỗ trợ?</h3>
                    <p>
                        📞 Hotline: 1900 1234<br>
                        📧 Email: support@qrpatrol.vn<br>
                        💬 Chat: <a href="https://m.me/qrpatrol">Facebook Messenger</a>
                    </p>
                </div>
                
                <div style="background: #f1f5f9; padding: 20px; text-align: center; color: #64748b; font-size: 12px;">
                    <p>© 2025 QR Patrol by Đại Sơn Long Security. All rights reserved.</p>
                </div>
            </div>
            """, customerName, companyName, username, password);
    }
}
