// File: QrCodeRepository.java
package com.example.demo.Repository;

import com.example.demo.Model.QrCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {

    @Query(value = "SELECT q FROM QrCode q WHERE " +
            "(:keyword IS NULL OR LOWER(q.locationName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR q.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR q.createdAt <= :endDate)",
            countQuery = "SELECT count(q) FROM QrCode q WHERE " +
                    "(:keyword IS NULL OR LOWER(q.locationName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                    "AND (:startDate IS NULL OR q.createdAt >= :startDate) " +
                    "AND (:endDate IS NULL OR q.createdAt <= :endDate)")
    Page<QrCode> search(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT q.idCode FROM QrCode q WHERE " +
            "(:keyword IS NULL OR LOWER(q.locationName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(q.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:startDate IS NULL OR q.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR q.createdAt <= :endDate)")
    List<UUID> findIdsBySearchCriteria(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}