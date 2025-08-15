package com.example.demo.service;

import com.example.demo.Model.TrialAccount;
import com.example.demo.dto.TrialRegistrationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TrialAccountService {

    void logTrialRegistration(TrialRegistrationDto registration, String username, boolean emailSent);
    Map<String, String> createTrialAccount(TrialRegistrationDto registration);
    List<TrialAccount> findAll();
    Optional<TrialAccount> findById(Long id);
    TrialAccount updateTrialAccount(TrialAccount trialAccount);
    void deleteTrialAccountById(Long id);
    TrialAccount extendTrial(Long id, int days);

    Page<TrialAccount> findActiveTrials(String keyword, String daysLeft, String companySize, boolean highActivity, boolean expiringSoon, Pageable pageable);
    Page<TrialAccount> findInactiveTrials(String keyword, String status, Pageable pageable);
    Map<String, Long> getActiveStats();
    Page<TrialAccount> findExpiredTrials(String keyword, Pageable pageable);
    Map<String, Long> getExpiredStats();
}