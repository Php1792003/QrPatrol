// src/main/java/com/example/demo/service/VehicleViolationService.java
package com.example.demo.service;

import com.example.demo.Model.VehicleViolation;
import org.springframework.data.domain.Page;
// Đảm bảo import ĐÚNG Pageable
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface VehicleViolationService {

    Page<VehicleViolation> findAll(Pageable pageable);

    Optional<VehicleViolation> findById(Long id);

    void saveViolation(VehicleViolation violation, MultipartFile imageFile, String creatorUsername);

    void deleteById(Long id);

    Optional<VehicleViolation> findLatestForResident(String licensePlate, String apartmentCode);

    List<VehicleViolation> findAllForResident(String licensePlate, String apartmentCode);

    Page<VehicleViolation> search(String keyword, Pageable pageable);
}