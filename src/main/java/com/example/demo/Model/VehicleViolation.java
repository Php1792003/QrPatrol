// Đường dẫn file: src/main/java/com/example/demo/Model/VehicleViolation.java

package com.example.demo.Model;

import com.example.demo.Enum.PenaltyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_violations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", length = 20, nullable = false)
    private String licensePlate;

    @Column(name = "apartment_code", length = 20, nullable = false)
    private String apartmentCode;

    @Column(name = "violation_date", nullable = false)
    private LocalDateTime violationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private PenaltyStatus status;

    @Nationalized // Hỗ trợ lưu trữ ký tự Unicode (tiếng Việt có dấu)
    @Column(name = "sanction_details", length = 255)
    private String sanctionDetails;

    @Column(name = "evidence_image", length = 255, nullable = true)
    private String evidenceImage;

    @Nationalized
    @Column(length = 500)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

}