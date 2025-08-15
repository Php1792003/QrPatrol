package com.example.demo.service.impl;

import com.example.demo.Enum.TrialActivityLevel;
import com.example.demo.Enum.TrialStatus;
import com.example.demo.Model.*;
import com.example.demo.Repository.RoleRepository;
import com.example.demo.Repository.TrialAccountRepository;
import com.example.demo.Repository.TrialLogRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.dto.TrialRegistrationDto;
import com.example.demo.service.TrialAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrialAccountServiceImpl implements TrialAccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TrialAccountRepository trialAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrialLogRepository trialLogRepository;

    @Autowired
    public TrialAccountServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                                   TrialAccountRepository trialAccountRepository, PasswordEncoder passwordEncoder,
                                   TrialLogRepository trialLogRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.trialAccountRepository = trialAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.trialLogRepository = trialLogRepository;
    }
    @Override
    public Page<TrialAccount> findActiveTrials(String keyword, String daysLeft, String companySize, boolean highActivity, boolean expiringSoon, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAfter = null;
        LocalDateTime expiresBefore = null;

        if (expiringSoon) {
            expiresBefore = now.plusDays(7);
        }

        if (daysLeft != null && !daysLeft.isEmpty()) {
            if (daysLeft.equals("30+")) {
                expiresAfter = now.plusDays(30);
            } else {
                try {
                    String[] parts = daysLeft.split("-");
                    expiresAfter = now.plusDays(Integer.parseInt(parts[0]));
                    expiresBefore = now.plusDays(Integer.parseInt(parts[1]));
                } catch (Exception ignored) {}
            }
        }

        String effectiveKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String effectiveCompanySize = (companySize != null && !companySize.isEmpty()) ? companySize : null;

        return trialAccountRepository.findActiveTrials(effectiveKeyword, effectiveCompanySize, expiresAfter, expiresBefore, highActivity, pageable);
    }

    @Override
    @Transactional
    public Map<String, String> createTrialAccount(TrialRegistrationDto registration) {
        String username = registration.getEmail().trim().toLowerCase();

        // Kiểm tra sự tồn tại của user hoặc trial account
        if (userRepository.findByUsername(username).isPresent() || trialAccountRepository.existsByEmail(username)) {
            throw new IllegalStateException("Email này đã được sử dụng để đăng ký.");
        }

        String rawPassword = generatePassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // SỬA LỖI: Luôn tìm role với tiền tố "ROLE_"
        Role trialRole = roleRepository.findByNameRole("VIEWER")
                .orElseThrow(() -> new RuntimeException("Lỗi cấu hình hệ thống: Role 'ROLE_VIEWER' không tồn tại."));

        // Tạo và lưu User trước
        User trialUser = new User();
        trialUser.setUsername(username);
        trialUser.setPassword(encodedPassword);
        trialUser.setFullName(registration.getFullName());
        trialUser.setEnabled(true);
        trialUser.setRoles(Collections.singleton(trialRole));
        User savedUser = userRepository.save(trialUser);

        // Tạo và lưu TrialAccount
        TrialAccount trialAccount = new TrialAccount();
        trialAccount.setEmail(username);
        trialAccount.setFullName(registration.getFullName());
        trialAccount.setPhone(registration.getPhone());
        trialAccount.setCompanyName(registration.getCompanyName());
        trialAccount.setCompanySize(registration.getCompanySize());
        trialAccount.setIndustry(registration.getIndustry());
        trialAccount.setCreatedAt(LocalDateTime.now());
        trialAccount.setExpiresAt(LocalDateTime.now().plusDays(30));
        trialAccount.setActive(true);
        trialAccount.setStatus(TrialStatus.ACTIVE);

        trialAccount.setPassword(rawPassword);

        trialAccount.setUser(savedUser);

        trialAccountRepository.save(trialAccount);

        // Ghi log
        logTrialRegistration(registration, username, true); // Giả sử email luôn gửi thành công

        Map<String, String> accountInfo = new HashMap<>();
        accountInfo.put("username", username);
        accountInfo.put("password", rawPassword);

        System.out.println("✅ Đã tạo và lưu tài khoản dùng thử cho: " + username);
        return accountInfo;
    }

    @Override
    @Transactional
    public void deleteTrialAccountById(Long id) {
        // findById sẽ tự động ném ra lỗi nếu không tìm thấy, không cần if/else
        TrialAccount trialAccount = trialAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản dùng thử với ID: " + id));

        // User được liên kết sẽ tự động bị xóa nhờ `orphanRemoval = true` trong TrialAccount model
        // Chúng ta không cần xóa User thủ công nữa, code sẽ sạch hơn.
        trialAccountRepository.delete(trialAccount);
    }

    @Override
    @Transactional
    public TrialAccount extendTrial(Long id, int days) {
        TrialAccount trialAccount = trialAccountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản dùng thử với ID: " + id));

        trialAccount.setExpiresAt(trialAccount.getExpiresAt().plusDays(days));

        // Nếu tài khoản hết hạn/bị treo, kích hoạt lại nó và cả User liên quan
        if (!trialAccount.isActive() || trialAccount.getStatus() != TrialStatus.ACTIVE) {
            trialAccount.setActive(true);
            trialAccount.setStatus(TrialStatus.ACTIVE);

            User user = trialAccount.getUser();
            if (user != null) {
                user.setEnabled(true);
                userRepository.save(user);
            }
        }
        return trialAccountRepository.save(trialAccount);
    }

    public void logTrialRegistration(TrialRegistrationDto registration, String username, boolean emailSent) {
        try {
            TrialLog log = new TrialLog();
            log.setFullName(registration.getFullName());
            log.setEmail(registration.getEmail());
            log.setPhone(registration.getPhone());
            log.setCompanyName(registration.getCompanyName());
            log.setCompanySize(registration.getCompanySize());
            log.setIndustry(registration.getIndustry());
            log.setExpectedUsers(registration.getExpectedUsers());
            log.setUseCase(registration.getUseCase());
            log.setCreatedUsername(username);
            log.setEmailSent(emailSent);
            log.setRegisteredAt(LocalDateTime.now());
            trialLogRepository.save(log);
            System.out.println("📊 Đã lưu log đăng ký dùng thử cho: " + registration.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu log đăng ký dùng thử: " + e.getMessage());
        }
    }

    private String generatePassword() {
        String chars = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        Random random = new java.security.SecureRandom();

        for (int i = 0; i < 10; i++) { // Tăng độ dài mật khẩu lên 10
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    @Override
    public List<TrialAccount> findAll() {
        return trialAccountRepository.findAll();
    }

    @Override
    public Optional<TrialAccount> findById(Long id) {
        return trialAccountRepository.findById(id);
    }

    @Override
    public TrialAccount updateTrialAccount(TrialAccount trialAccount) {
        return trialAccountRepository.save(trialAccount);
    }
    @Override
    public Page<TrialAccount> findInactiveTrials(String keyword, String status, Pageable pageable) {
        String effectiveKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        List<TrialStatus> statusesToSearch = new ArrayList<>();

        if (status != null && !status.isEmpty()) {
            try {
                statusesToSearch.add(TrialStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException e) {
                statusesToSearch.addAll(Arrays.asList(TrialStatus.EXPIRED, TrialStatus.SUSPENDED));
            }
        } else {
            statusesToSearch.addAll(Arrays.asList(TrialStatus.EXPIRED, TrialStatus.SUSPENDED));
        }
        return trialAccountRepository.findInactiveTrials(effectiveKeyword, statusesToSearch, pageable);
    }

    @Override
    public Page<TrialAccount> findExpiredTrials(String keyword, Pageable pageable) {
        String effectiveKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        List<TrialStatus> statuses = Collections.singletonList(TrialStatus.EXPIRED);
        return trialAccountRepository.findInactiveTrials(effectiveKeyword, statuses, pageable);
    }

    @Override
    public Map<String, Long> getExpiredStats() {
        Map<String, Long> stats = new HashMap<>();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime sevenDaysAgo = today.minusDays(7);

        stats.put("totalExpiredTrials", trialAccountRepository.countByStatus(TrialStatus.EXPIRED));
        stats.put("expiredToday", trialAccountRepository.countExpiredSince(today));
        stats.put("expiredThisWeek", trialAccountRepository.countExpiredSince(sevenDaysAgo));
        stats.put("highPotentialExpired", trialAccountRepository.countHighPotentialExpired());
        stats.put("renewedThisMonth", 0L);

        return stats;
    }

    @Override
    public Map<String, Long> getActiveStats() {
        Map<String, Long> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        stats.put("totalActiveTrials", trialAccountRepository.countByStatus(TrialStatus.ACTIVE));
        stats.put("expiringTrials", trialAccountRepository.countExpiringBetween(now, now.plusDays(7)));
        stats.put("highActivityTrials", trialAccountRepository.countByStatusAndActivityLevel(TrialStatus.ACTIVE, TrialActivityLevel.HIGH));
        stats.put("potentialTrials", 34L); // Dữ liệu giả

        return stats;
    }

    @Transactional
    public void updateLeadScore(Long trialAccountId) {
        TrialAccount trial = trialAccountRepository.findById(trialAccountId).orElse(null);
        if (trial != null) {
            int score = 0;
            // Logic tính điểm
            if (trial.getLoginCount() > 5) {
                score += 30;
            }
            if (trial.getCompanySize().equals("51-200 nhân viên") || trial.getCompanySize().equals("200+ nhân viên")) {
                score += 40;
            }
            if (trial.getIndustry() != null && trial.getIndustry().equalsIgnoreCase("Technology")) {
                score += 20;
            }

            // Đảm bảo điểm không vượt quá 100
            trial.setLeadScore(Math.min(score, 100));
            trialAccountRepository.save(trial);
        }
    }
}