package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patrol_logs")
@Data
public class PatrolLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPatrolLog;

    @Column(name = "qr_code_id", nullable = false)
    private UUID qrCodeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Nationalized
    @Column(name = "scanner_name", nullable = false)
    private String scannerName;

    @Nationalized
    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Column(name = "scanned_at", nullable = false)
    private LocalDateTime scannedAt;

}
