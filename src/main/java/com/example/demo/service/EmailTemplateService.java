package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailTemplateService {

    public String createCustomerConfirmationTemplate(String customerName, String subject, String message) {
        return String.format("""
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Chào mừng đến với QR Patrol</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f5; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; }
                    .header { background: linear-gradient(135deg, #6366F1, #8B5CF6); padding: 40px 30px; text-align: center; }
                    .header h1 { color: white; margin: 0; font-size: 28px; font-weight: bold; }
                    .content { padding: 40px 30px; }
                    .credentials-box { background: #e0e7ff; border: 2px solid #6366F1; border-radius: 8px; padding: 25px; margin: 25px 0; }
                    .credential-value { background: #ddd6fe; padding: 8px 12px; border-radius: 6px; font-family: monospace; font-weight: bold; color: #5b21b6; }
                    .cta-button { display: inline-block; background: #6366F1; color: white; padding: 15px 30px; text-decoration: none; border-radius: 8px; font-weight: bold; }
                    .footer { background: #1f2937; color: white; padding: 30px; text-align: center; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 Chào mừng đến với QR Patrol!</h1>
                        <p style="color: rgba(255,255,255,0.9); margin: 10px 0 0 0;">Tài khoản dùng thử 30 ngày của bạn đã sẵn sàng</p>
                    </div>
                    <div class="content">
                        <h2>Chào %s,</h2>
                        <p>Cảm ơn bạn đã đăng ký dùng thử QR Patrol! Tài khoản của <strong>%s</strong> đã được thiết lập thành công.</p>
                        
                        <div class="credentials-box">
                            <h3 style="color: #4338ca; margin: 0 0 15px 0;">🔐 Thông tin đăng nhập:</h3>
                            <p><strong>Tên đăng nhập:</strong> <span class="credential-value">%s</span></p>
                            <p><strong>Mật khẩu:</strong> <span class="credential-value">%s</span></p>
                            <p><strong>Thời gian dùng thử:</strong> <span style="color: #059669; font-weight: bold;">30 ngày miễn phí</span></p>
                            <p><strong>Ngày hết hạn:</strong> <span style="color: #dc2626; font-weight: bold;">%s</span></p>
                        </div>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="https://app.qrpatrol.vn" class="cta-button">🚀 Đăng nhập ngay</a>
                        </div>
                        
                        <h3>🚀 Bắt đầu nhanh chóng:</h3>
                        <ol style="line-height: 1.8;">
                            <li>Đăng nhập vào hệ thống bằng thông tin ở trên</li>
                            <li>Tạo điểm tuần tra đầu tiên của bạn</li>
                            <li>Tải ứng dụng mobile cho nhân viên bảo vệ</li>
                            <li>Bắt đầu quét QR code và theo dõi báo cáo</li>
                        </ol>
                        
                        <div style="background: #fef3c7; border: 1px solid #f59e0b; border-radius: 8px; padding: 20px; margin: 25px 0;">
                            <h3>💬 Cần hỗ trợ?</h3>
                            <p><strong>Hotline:</strong> 1900 1234 (8:00-18:00, T2-T6)<br>
                            <strong>Email:</strong> support@qrpatrol.vn<br>
                            <strong>Chat:</strong> <a href="https://m.me/qrpatrol">Facebook Messenger</a></p>
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>QR Patrol by Đại Sơn Long Security</strong><br>
                        Giải pháp quản lý tuần tra thông minh từ kinh nghiệm 15 năm</p>
                        <p style="margin-top: 15px;">© 2025 QR Patrol. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, customerName, subject, message);
    }

    public String createAdminNotificationTemplate(String customerName, String customerEmail,
                                                  String customerPhone, String company,
                                                  String subject, String message, boolean newsletter) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return String.format("""
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>QR Patrol - Tin nhắn liên hệ mới</title>
            <script src="https://cdn.tailwindcss.com"></script>
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');
                body { font-family: 'Inter', sans-serif; }
            </style>
        </head>
        <body class="bg-gray-50">
            <div class="max-w-4xl mx-auto bg-white shadow-lg rounded-lg overflow-hidden">
                <!-- Header -->
                <div class="bg-gradient-to-r from-red-600 to-orange-600 px-6 py-6">
                    <div class="flex items-center justify-between">
                        <div class="flex items-center">
                            <div class="w-10 h-10 bg-white rounded-lg flex items-center justify-center mr-3">
                                <svg class="w-6 h-6 text-red-600" fill="currentColor" viewBox="0 0 20 20">
                                    <path d="M10 2L3 7v11a1 1 0 001 1h3a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1h3a1 1 0 001-1V7l-7-5z"/>
                                </svg>
                            </div>
                            <div>
                                <h1 class="text-xl font-bold text-white">QR Patrol Admin</h1>
                                <p class="text-red-100 text-sm">Tin nhắn liên hệ mới</p>
                            </div>
                        </div>
                        <div class="text-right text-white">
                            <p class="text-sm opacity-90">%s</p>
                            <div class="flex items-center">
                                <div class="w-2 h-2 bg-green-400 rounded-full mr-2 animate-pulse"></div>
                                <span class="text-xs">Tin nhắn mới</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Customer Information -->
                <div class="p-6">
                    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
                        <!-- Customer Details -->
                        <div class="bg-blue-50 border border-blue-200 rounded-lg p-6">
                            <h3 class="text-lg font-semibold text-blue-900 mb-4 flex items-center">
                                <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                                </svg>
                                Thông tin khách hàng
                            </h3>
                            <div class="space-y-3">
                                <div class="flex items-center">
                                    <span class="w-20 text-sm font-medium text-blue-800">Tên:</span>
                                    <span class="text-gray-900 font-semibold">%s</span>
                                </div>
                                <div class="flex items-center">
                                    <span class="w-20 text-sm font-medium text-blue-800">Email:</span>
                                    <a href="mailto:%s" class="text-blue-600 hover:text-blue-800 underline">%s</a>
                                </div>
                                %s
                                %s
                            </div>
                        </div>

                        <!-- Message Info -->
                        <div class="bg-purple-50 border border-purple-200 rounded-lg p-6">
                            <h3 class="text-lg font-semibold text-purple-900 mb-4 flex items-center">
                                <svg class="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M3 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clip-rule="evenodd"/>
                                </svg>
                                Chi tiết yêu cầu
                            </h3>
                            <div class="space-y-3">
                                <div>
                                    <span class="text-sm font-medium text-purple-800">Chủ đề:</span>
                                    <div class="mt-1">
                                        <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-purple-100 text-purple-800">
                                            %s
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Message Content -->
                    <div class="bg-gray-50 border rounded-lg p-6 mb-6">
                        <h3 class="text-lg font-semibold text-gray-900 mb-4">Nội dung tin nhắn</h3>
                        <div class="bg-white border rounded p-4 text-gray-800 leading-relaxed">
                            %s
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="flex flex-wrap gap-4">
                        <a href="mailto:%s?subject=Re: %s" 
                           class="inline-flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
                            <svg class="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                                <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"/>
                                <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"/>
                            </svg>
                            Phản hồi Email
                        </a>
                        %s
                    </div>
                </div>

                <!-- Footer -->
                <div class="bg-gray-900 text-white px-6 py-4 text-center">
                    <p class="text-sm">
                        QR Patrol Admin Panel | 
                        <a href="#" class="text-blue-400 hover:text-blue-300">Đăng nhập Admin</a>
                    </p>
                </div>
            </div>
        </body>
        </html>
        """,
                timestamp,
                customerName,
                customerEmail, customerEmail,
                customerPhone != null && !customerPhone.isEmpty() ?
                        String.format("<div class=\"flex items-center\"><span class=\"w-20 text-sm font-medium text-blue-800\">SĐT:</span><a href=\"tel:%s\" class=\"text-blue-600 hover:text-blue-800\">%s</a></div>", customerPhone, customerPhone) : "",
                company != null && !company.isEmpty() ?
                        String.format("<div class=\"flex items-center\"><span class=\"w-20 text-sm font-medium text-blue-800\">Công ty:</span><span class=\"text-gray-900\">%s</span></div>", company) : "",
                subject,
                message,
                customerEmail, subject,
                customerPhone != null && !customerPhone.isEmpty() ?
                        String.format("<a href=\"tel:%s\" class=\"inline-flex items-center px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors\"><svg class=\"w-4 h-4 mr-2\" fill=\"currentColor\" viewBox=\"0 0 20 20\"><path d=\"M2 3a1 1 0 011-1h2.153a1 1 0 01.986.836l.74 4.435a1 1 0 01-.54 1.06l-1.548.773a11.037 11.037 0 006.105 6.105l.774-1.548a1 1 0 011.059-.54l4.435.74a1 1 0 01.836.986V17a1 1 0 01-1 1h-2C7.82 18 2 12.18 2 5V3z\"/></svg>Gọi điện</a>", customerPhone) : ""
        );
    }

    // New method for trial confirmation template
    public String createTrialConfirmationTemplate(String customerName, String username, String password, String companyName) {
        String expiryDate = LocalDateTime.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return String.format("""
            <div style="max-width: 600px; margin: 0 auto; font-family: 'Inter', sans-serif;">
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
                        📞 Hotline: 0905 441 263<br>
                        📧 Email: support@qrpatrol.vn<br>
                        💬 Chat: <a href="https://www.facebook.com/phucsisme">Facebook Messenger</a>
                    </p>
                </div>
                
                <div style="background: #f1f5f9; padding: 20px; text-align: center; color: #64748b; font-size: 12px;">
                    <p>© 2025 QR Patrol by Đại Sơn Long Security. All rights reserved.</p>
                </div>
            </div>
            """, customerName, companyName, username, password, expiryDate);
    }

    // New method for welcome email template
    public String createWelcomeEmailTemplate(String customerName, String companyName) {
        return String.format("""
            <div style="max-width: 600px; margin: 0 auto; font-family: 'Inter', sans-serif;">
                <div style="background: linear-gradient(135deg, #10B981, #059669); padding: 30px; text-align: center; color: white;">
                    <h1>🎉 Chào mừng đến với QR Patrol!</h1>
                    <p>Chúng tôi rất vui khi có bạn tham gia</p>
                </div>
                
                <div style="padding: 30px; background: white;">
                    <h2>Xin chào %s,</h2>
                    <p>Chào mừng <strong>%s</strong> đến với gia đình QR Patrol! Chúng tôi rất hân hạnh được phục vụ bạn.</p>
                    
                    <h3>🚀 Bắt đầu ngay:</h3>
                    <ol>
                        <li>Đăng nhập vào hệ thống</li>
                        <li>Tạo điểm tuần tra đầu tiên</li>
                        <li>Thêm nhân viên bảo vệ</li>
                        <li>Bắt đầu tuần tra</li>
                    </ol>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="https://app.qrpatrol.vn" style="background: #10B981; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; display: inline-block;">
                            Bắt đầu ngay
                        </a>
                    </div>
                </div>
                
                <div style="background: #f1f5f9; padding: 20px; text-align: center; color: #64748b; font-size: 12px;">
                    <p>© 2025 QR Patrol by Đại Sơn Long Security. All rights reserved.</p>
                </div>
            </div>
            """, customerName, companyName);
    }

    // New method for expiry reminder template
    public String createExpiryReminderTemplate(String customerName, String username, int daysLeft) {
        return String.format("""
            <div style="max-width: 600px; margin: 0 auto; font-family: 'Inter', sans-serif;">
                <div style="background: linear-gradient(135deg, #F59E0B, #D97706); padding: 30px; text-align: center; color: white;">
                    <h1>⏰ Tài khoản sắp hết hạn</h1>
                    <p>Còn %d ngày để gia hạn</p>
                </div>
                
                <div style="padding: 30px; background: white;">
                    <h2>Chào %s,</h2>
                    <p>Tài khoản <strong>%s</strong> của bạn sẽ hết hạn trong <strong>%d ngày</strong>.</p>
                    
                    <div style="background: #FEF3C7; border: 2px solid #F59E0B; border-radius: 8px; padding: 20px; margin: 20px 0;">
                        <h3>💡 Để tiếp tục sử dụng QR Patrol:</h3>
                        <p>Liên hệ với chúng tôi để được tư vấn gói phù hợp với doanh nghiệp của bạn.</p>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="tel:19001234" style="background: #F59E0B; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; display: inline-block; margin-right: 10px;">
                            📞 Gọi ngay
                        </a>
                        <a href="mailto:sales@qrpatrol.vn" style="background: #6366F1; color: white; padding: 12px 24px; text-decoration: none; border-radius: 6px; display: inline-block;">
                            📧 Email tư vấn
                        </a>
                    </div>
                </div>
                
                <div style="background: #f1f5f9; padding: 20px; text-align: center; color: #64748b; font-size: 12px;">
                    <p>© 2025 QR Patrol by Đại Sơn Long Security. All rights reserved.</p>
                </div>
            </div>
            """, daysLeft, customerName, username, daysLeft);
    }
}