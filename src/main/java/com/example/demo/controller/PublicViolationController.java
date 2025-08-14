// src/main/java/com/example/demo/controller/PublicViolationController.java
package com.example.demo.controller;

import com.example.demo.Model.VehicleViolation;
import com.example.demo.service.VehicleViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PublicViolationController {

    @Autowired
    private VehicleViolationService violationService;

    @GetMapping("/violation-search")
    public String showSearchPage(Model model) {
        model.addAttribute("pageTitle", "Tra cứu vi phạm đỗ xe");
        return "violation_search";
    }

    @PostMapping("/violation-search")
    public String searchViolation(@RequestParam String licensePlate,
                                  @RequestParam String apartmentCode,
                                  Model model) {
        List<VehicleViolation> violations = violationService.findAllForResident(licensePlate.trim(), apartmentCode.trim());

        if (violations.isEmpty()) {
            model.addAttribute("notFound", true);
        } else {
            model.addAttribute("violations", violations);
        }

        model.addAttribute("pageTitle", "Kết quả tra cứu");
        model.addAttribute("searchedLicensePlate", licensePlate);
        model.addAttribute("searchedApartmentCode", apartmentCode);
        return "violation_search";
    }
}