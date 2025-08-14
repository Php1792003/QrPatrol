package com.example.demo.controller;

import com.example.demo.Model.QrCode;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.PatrolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Controller
public class ScannerController {

    @Autowired
    private PatrolService patrolService;

    @GetMapping("/scanner/scan")
    public String scanPage(Model model) {
        model.addAttribute("currentPage", "scan_qr");
        return "scanner/scan";
    }

    @PostMapping("/api/scanner/record")
    @ResponseBody // Trả về JSON, không phải tên view
    public Map<String, Object> recordScan(@RequestBody Map<String, String> payload,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        String qrCodeId = payload.get("qrCodeId");

        Optional<QrCode> scannedQr = patrolService.recordScan(qrCodeId, userDetails.getUser());

        if (scannedQr.isPresent()) {
            QrCode qrCode = scannedQr.get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
            String formattedTime = LocalDateTime.now().format(formatter);

            return Map.of(
                    "success", true,
                    "location", qrCode.getLocationName(),
                    "time", formattedTime
            );
        } else {
            return Map.of(
                    "success", false,
                    "message", "Mã QR không hợp lệ hoặc không tồn tại."
            );
        }
    }
}