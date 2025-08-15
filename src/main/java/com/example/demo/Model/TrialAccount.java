// Vị trí: src/main/java/com/example/demo/Model/TrialAccount.java
package com.example.demo.Model;

import com.example.demo.Enum.TrialActivityLevel;
import com.example.demo.Enum.TrialStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "trial_accounts")
@Getter
@Setter
public class TrialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String password; // Mật khẩu gốc, chưa mã hóa
    private String companyName;
    private String companySize;
    private String industry;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;
    private LocalDateTime lastLoginAt;
    private int loginCount = 0;
    private int leadScore = 0;
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private TrialActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private TrialStatus status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private User user;

    // --- Getters động cho Thymeleaf ---
    public String getUsername() { return (this.user != null) ? this.user.getUsername() : "N/A"; }
    public LocalDateTime getStartDate() { return this.createdAt; }
    public LocalDateTime getExpiryDate() { return this.expiresAt; }
    public long getDaysLeft() {
        if (this.expiresAt == null || this.status != TrialStatus.ACTIVE) return 0;
        Duration duration = Duration.between(LocalDateTime.now(), this.expiresAt);
        return duration.isNegative() ? 0 : duration.toDays() + 1;
    }
}