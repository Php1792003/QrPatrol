package com.example.demo.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial_logs")
public class TrialLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Thông tin người đăng ký
    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String companyName;

    // Các thông tin bổ sung từ form
    private String companySize;
    private String industry;
    private String expectedUsers;

    @Column(columnDefinition = "TEXT")
    private String useCase;

    // Thông tin tài khoản được tạo
    @Column(nullable = false)
    private String createdUsername;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private boolean emailSent;

    // Constructors
    public TrialLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanySize() { return companySize; }
    public void setCompanySize(String companySize) { this.companySize = companySize; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getExpectedUsers() { return expectedUsers; }
    public void setExpectedUsers(String expectedUsers) { this.expectedUsers = expectedUsers; }
    public String getUseCase() { return useCase; }
    public void setUseCase(String useCase) { this.useCase = useCase; }
    public String getCreatedUsername() { return createdUsername; }
    public void setCreatedUsername(String createdUsername) { this.createdUsername = createdUsername; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    public boolean isEmailSent() { return emailSent; }
    public void setEmailSent(boolean emailSent) { this.emailSent = emailSent; }
}