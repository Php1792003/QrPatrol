package com.example.demo.controller;

import com.example.demo.Model.QrCode;
import com.example.demo.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/viewer")
public class ViewerController {

    private final QrCodeService qrCodeService;

    @Autowired
    public ViewerController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/qrcodes")
    public String listQrCodes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(LocalTime.MAX) : null;
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        Page<QrCode> qrCodePage = qrCodeService.search(keyword, startDateTime, endDateTime, pageable);

        model.addAttribute("qrCodePage", qrCodePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/qrcodes_list";
    }
}
