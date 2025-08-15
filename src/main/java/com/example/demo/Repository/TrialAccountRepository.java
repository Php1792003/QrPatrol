// Vị trí: src/main/java/com/example/demo/Repository/TrialAccountRepository.java
package com.example.demo.Repository;

import com.example.demo.Enum.TrialActivityLevel;
import com.example.demo.Enum.TrialStatus;
import com.example.demo.Model.TrialAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrialAccountRepository extends JpaRepository<TrialAccount, Long> {

    boolean existsByEmail(String email);


    @Query("SELECT t FROM TrialAccount t WHERE " +
            "t.status = :status " +
            "AND (:keyword IS NULL OR (LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND (:companySize IS NULL OR t.companySize = :companySize) " +
            "AND (:expiresAfter IS NULL OR t.expiresAt >= :expiresAfter) " +
            "AND (:expiresBefore IS NULL OR t.expiresAt <= :expiresBefore) " +
            "AND (:highActivity IS FALSE OR t.activityLevel = com.example.demo.Enum.TrialActivityLevel.HIGH)")
    Page<TrialAccount> findByStatusAndFilters(
            @Param("status") TrialStatus status,
            @Param("keyword") String keyword,
            @Param("companySize") String companySize,
            @Param("expiresAfter") LocalDateTime expiresAfter,
            @Param("expiresBefore") LocalDateTime expiresBefore,
            @Param("highActivity") boolean highActivity,
            Pageable pageable);

    @Query("SELECT t FROM TrialAccount t WHERE " +
            "(t.status IN :statuses) " +
            "AND (:keyword IS NULL OR (LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<TrialAccount> findByStatusesAndKeyword(
            @Param("keyword") String keyword,
            @Param("statuses") List<TrialStatus> statuses,
            Pageable pageable);

    @Query("SELECT t FROM TrialAccount t WHERE " +
            "t.status = com.example.demo.Enum.TrialStatus.ACTIVE " + // Sửa lại đường dẫn Enum
            "AND (:keyword IS NULL OR (LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))) " +
            "AND (:companySize IS NULL OR t.companySize = :companySize) " +
            "AND (:expiresAfter IS NULL OR t.expiresAt >= :expiresAfter) " +
            "AND (:expiresBefore IS NULL OR t.expiresAt <= :expiresBefore) " +
            "AND (:highActivity IS FALSE OR t.activityLevel = com.example.demo.Enum.TrialActivityLevel.HIGH)") // Sửa lại đường dẫn Enum
    Page<TrialAccount> findActiveTrials(
            @Param("keyword") String keyword,
            @Param("companySize") String companySize,
            @Param("expiresAfter") LocalDateTime expiresAfter,
            @Param("expiresBefore") LocalDateTime expiresBefore,
            @Param("highActivity") boolean highActivity,
            Pageable pageable);

    @Query("SELECT t FROM TrialAccount t WHERE " +
            "(t.status IN :statuses) " +
            "AND (:keyword IS NULL OR (LOWER(t.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<TrialAccount> findInactiveTrials(
            @Param("keyword") String keyword,
            @Param("statuses") List<TrialStatus> statuses,
            Pageable pageable);

    long countByStatus(TrialStatus status);

    long countByStatusAndActivityLevel(TrialStatus status, TrialActivityLevel level);

    @Query("SELECT count(t) FROM TrialAccount t WHERE t.status = com.example.demo.Enum.TrialStatus.ACTIVE AND t.expiresAt BETWEEN :start AND :end")
    long countExpiringBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // THÊM LẠI PHƯƠNG THỨC BỊ THIẾU
    @Query("SELECT count(t) FROM TrialAccount t WHERE t.status = com.example.demo.Enum.TrialStatus.EXPIRED AND t.expiresAt >= :sinceDate")
    long countExpiredSince(@Param("sinceDate") LocalDateTime sinceDate);

    // THÊM LẠI PHƯƠNG THỨC BỊ THIẾU
    @Query("SELECT count(t) FROM TrialAccount t WHERE t.status = com.example.demo.Enum.TrialStatus.EXPIRED AND t.leadScore > 70")
    long countHighPotentialExpired();

}