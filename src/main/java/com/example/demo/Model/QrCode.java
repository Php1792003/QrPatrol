package com.example.demo.Model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "qr_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QrCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID idCode;

    @Nationalized
    @Column(name = "location_name", nullable = false)
    private String locationName;

    @Nationalized
    @Column(length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "create_by", nullable = false)
    private String createdBy;
}
