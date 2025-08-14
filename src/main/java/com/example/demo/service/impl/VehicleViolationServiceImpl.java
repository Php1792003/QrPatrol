package com.example.demo.service.impl;

import com.example.demo.Model.PenaltyStatus;
import com.example.demo.Model.User;
import com.example.demo.Model.VehicleViolation;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Repository.VehicleViolationRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.VehicleViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleViolationServiceImpl implements VehicleViolationService {

    @Autowired private VehicleViolationRepository violationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FileStorageService fileStorageService;

    @Value("${file.upload-dir.violations}")
    private String violationUploadDir;

    @Override
    public List<VehicleViolation> findAllForResident(String licensePlate, String apartmentCode) {
        // Chỉ cần gọi phương thức repository và trả về kết quả
        return violationRepository.findByLicensePlateAndApartmentCodeOrderByViolationDateDesc(licensePlate, apartmentCode);
    }

    @Override
    public Page<VehicleViolation> search(String keyword, Pageable pageable) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        return violationRepository.search(keyword, pageable);
    }

    @Override
    public Page<VehicleViolation> findAll(Pageable pageable) {
        return violationRepository.findAll(pageable);
    }


    @Override
    public Optional<VehicleViolation> findById(Long id) {
        return violationRepository.findById(id);
    }

    @Override
    @Transactional
    public void saveViolation(VehicleViolation violation, MultipartFile imageFile, String creatorUsername) {
        String oldLicensePlate = null;
        boolean isNewViolation = violation.getId() == null;

        if (!isNewViolation) {
            oldLicensePlate = violationRepository.findById(violation.getId())
                    .map(VehicleViolation::getLicensePlate)
                    .orElse(null);
        }


        if (isNewViolation) {
            long previousViolations = violationRepository.countByLicensePlate(violation.getLicensePlate());

            if (previousViolations == 0) {
                violation.setStatus(PenaltyStatus.NHAC_NHO);
            } else if (previousViolations == 1) {
                violation.setStatus(PenaltyStatus.CANH_CAO);
            } else { // previousViolations >= 2
                violation.setStatus(PenaltyStatus.CHE_TAI);
            }

            violation.setViolationDate(LocalDateTime.now());
            User creator = userRepository.findByUsername(creatorUsername).orElse(null);
            violation.setCreatedBy(creator);
        }

        VehicleViolation savedViolation = violationRepository.save(violation);

        if (imageFile != null && !imageFile.isEmpty()) {
            String fileExtension = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
            String newFileName = "violation-" + savedViolation.getId() + "." + fileExtension;
            fileStorageService.storeFile(imageFile, newFileName, violationUploadDir);
            savedViolation.setEvidenceImage(newFileName);
            violationRepository.save(savedViolation);
        }

        if (!isNewViolation) {
            recalculatePenaltiesForLicensePlate(savedViolation.getLicensePlate());
            if (oldLicensePlate != null && !oldLicensePlate.equals(savedViolation.getLicensePlate())) {
                recalculatePenaltiesForLicensePlate(oldLicensePlate);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        VehicleViolation violationToDelete = violationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vi phạm với ID: " + id));

        String licensePlate = violationToDelete.getLicensePlate();
        String imageName = violationToDelete.getEvidenceImage();

        violationRepository.delete(violationToDelete);

        if (imageName != null && !imageName.isEmpty()) {
            fileStorageService.deleteFile(imageName, violationUploadDir);
        }

        recalculatePenaltiesForLicensePlate(licensePlate);
    }

    @Override
    public Optional<VehicleViolation> findLatestForResident(String licensePlate, String apartmentCode) {
        List<VehicleViolation> violations = violationRepository.findByLicensePlateAndApartmentCodeOrderByViolationDateDesc(licensePlate, apartmentCode);
        return violations.stream().findFirst();
    }


    private void recalculatePenaltiesForLicensePlate(String licensePlate) {
        List<VehicleViolation> violations = violationRepository.findByLicensePlateOrderByViolationDateAsc(licensePlate);

        for (int i = 0; i < violations.size(); i++) {
            VehicleViolation currentViolation = violations.get(i);
            PenaltyStatus newStatus;

            if (i == 0) {
                newStatus = PenaltyStatus.NHAC_NHO;
            } else if (i == 1) {
                newStatus = PenaltyStatus.CANH_CAO;
            } else {
                newStatus = PenaltyStatus.CHE_TAI;
            }

            if (currentViolation.getStatus() != newStatus) {
                currentViolation.setStatus(newStatus);
                violationRepository.save(currentViolation);
            }
        }
    }
}