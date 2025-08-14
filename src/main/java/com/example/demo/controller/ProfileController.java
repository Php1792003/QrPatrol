// --- START OF FILE ProfileController.java ---
package com.example.demo.controller;

import com.example.demo.Model.User;
import com.example.demo.dto.ChangePasswordDto;
import com.example.demo.dto.ProfileDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping
    public String showProfile(Model model) {
        User user = userService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update-info")
    public String updateInfo(@ModelAttribute ProfileDto profileDto,
                             @RequestParam("avatarFile") MultipartFile avatarFile,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.updateProfile(getCurrentUsername(), profileDto, avatarFile);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute ChangePasswordDto passwordDto,
                                 RedirectAttributes redirectAttributes) {
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmNewPassword())) {
            redirectAttributes.addFlashAttribute("passwordError", "Mật khẩu mới không khớp.");
            return "redirect:/profile";
        }
        try {
            userService.changePassword(getCurrentUsername(), passwordDto.getOldPassword(), passwordDto.getNewPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("passwordError", e.getMessage());
        }
        return "redirect:/profile";
    }
}
