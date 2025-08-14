package com.example.demo.Repository;

import com.example.demo.Model.PatrolLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PatrolLogRepository extends JpaRepository<PatrolLog,Long> {
    List<PatrolLog> findByLocationNameContainingIgnoreCaseOrScannerNameContainingIgnoreCase(String locationKeyword, String scannerKeyword);

    long countByScannedAtBetween(LocalDateTime start, LocalDateTime end);

    List<PatrolLog> findTop5ByOrderByScannedAtDesc();

    @Query("SELECT p FROM PatrolLog p WHERE " +
            "(:keyword IS NULL OR LOWER(p.locationName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.scannerName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:startDate IS NULL OR p.scannedAt >= :startDate) " +
            "AND (:endDate IS NULL OR p.scannedAt <= :endDate) " +
            "ORDER BY p.scannedAt DESC")
    Page<PatrolLog> search(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query(value =
            "SELECT " +
                    "    DATEPART(hour, scanned_at) AS hour_of_day, " +
                    "    COUNT(*) AS scan_count " +
                    "FROM " +
                    "    patrol_logs " +
                    "WHERE " +
                    "    CAST(scanned_at AS DATE) = CAST(GETDATE() AS DATE) " + // Lọc theo ngày hôm nay
                    "GROUP BY " +
                    "    DATEPART(hour, scanned_at) " +
                    "ORDER BY " +
                    "    hour_of_day ASC",
            nativeQuery = true)
    List<Map<String, Object>> countScansByHourToday();
}
