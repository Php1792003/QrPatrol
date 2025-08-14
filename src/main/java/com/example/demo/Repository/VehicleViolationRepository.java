package com.example.demo.Repository;

import com.example.demo.Model.VehicleViolation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleViolationRepository extends JpaRepository<VehicleViolation, Long> {
    long countByLicensePlate(String licensePlate);

    List<VehicleViolation> findByLicensePlateAndApartmentCodeOrderByViolationDateDesc(String licensePlate, String apartmentCode);

    List<VehicleViolation> findByLicensePlateOrderByViolationDateAsc(String licensePlate);

    @Query("SELECT v FROM VehicleViolation v WHERE " +
            "(:keyword IS NULL OR LOWER(v.licensePlate) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.apartmentCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<VehicleViolation> search(@Param("keyword") String keyword, Pageable pageable);
}
