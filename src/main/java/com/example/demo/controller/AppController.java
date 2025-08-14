package com.example.demo.controller;

import com.example.demo.dto.ContactDTO;
import com.example.demo.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {

    @Autowired // Hoặc dùng constructor injection như của bạn
    private EmailService emailService;

    @GetMapping("/")
    public String showLandingPage() {
        return "landing";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return "home";
    }

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        model.addAttribute("contactDto", new ContactDTO());
        return "contact";
    }

    @PostMapping("/contact")
    public String handleContactForm(@Valid @ModelAttribute("contactDto") ContactDTO contactDto,
                                    BindingResult bindingResult, // Phải đặt ngay sau đối tượng @Valid
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "contact";
        }

        try {
            emailService.sendConfirmationToCustomer(contactDto.getEmail(), contactDto.getName());
            emailService.sendNotificationToAdmin(contactDto.getName(), contactDto.getEmail(), contactDto.getMessage());
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn! Tin nhắn của bạn đã được gửi thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã có lỗi xảy ra khi gửi tin nhắn.");
        }

        return "redirect:/contact";
    }
    @GetMapping("/pricing")
    public String showPricingPage() {
        return "pricing";
    }

    @GetMapping("/about")
    public String showAboutPage() {
        return "about";
    }
}