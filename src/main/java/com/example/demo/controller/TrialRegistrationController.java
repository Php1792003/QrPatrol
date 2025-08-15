package com.example.demo.controller;

import com.example.demo.dto.TrialRegistrationDto;
import com.example.demo.service.EmailService;
import com.example.demo.service.TrialAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.Map;
import java.util.HashMap;

@Controller
public class TrialRegistrationController {

    @Autowired
    private TrialAccountService trialAccountService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/trial-registration")
    public String trialRegistrationPage(Model model) {
        // Cung cấp một đối tượng DTO rỗng cho form
        model.addAttribute("registrationDto", new TrialRegistrationDto());
        return "trial-registration";
    }


    @PostMapping("/trial-registration")
    @ResponseBody
    public Map<String, Object> registerTrial(@ModelAttribute TrialRegistrationDto registrationDto) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("=== TRIAL REGISTRATION REQUEST ===");
            System.out.println("Full Name: " + registrationDto.getFullName());
            System.out.println("Email: " + registrationDto.getEmail());
            System.out.println("Phone: " + registrationDto.getPhone());
            System.out.println("Company: " + registrationDto.getCompanyName());

            Map<String, String> accountInfo = trialAccountService.createTrialAccount(registrationDto);
            String username = accountInfo.get("username");
            String password = accountInfo.get("password");

            emailService.sendTrialConfirmationEmail(
                    registrationDto.getEmail(),
                    registrationDto.getFullName(),
                    username,
                    password,
                    registrationDto.getCompanyName()
            );

            trialAccountService.logTrialRegistration(registrationDto, username, true);

            response.put("success", true);
            response.put("message", "Đăng ký thành công! Vui lòng kiểm tra email để nhận thông tin đăng nhập.");
            response.put("username", username);
            response.put("password", password);
            response.put("emailSent", true);

            System.out.println("✅ Trial registration successful for: " + registrationDto.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra trong quá trình đăng ký: " + e.getMessage());
            System.err.println("❌ Trial registration failed: " + e.getMessage());
        }

        return response;
    }
    }