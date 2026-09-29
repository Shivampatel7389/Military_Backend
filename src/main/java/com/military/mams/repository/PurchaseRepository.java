package com.military.mams.repository;

import com.military.mams.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE " +
           "(:baseId IS NULL OR p.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR p.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:equipmentId IS NULL OR p.equipment.id = :equipmentId) AND " +
           "(:startDate IS NULL OR p.purchaseDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.purchaseDate <= :endDate) " +
           "ORDER BY p.purchaseDate DESC, p.id DESC")
    List<Purchase> findWithFilters(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Purchase p WHERE " +
           "(:baseId IS NULL OR p.base.id = :baseId) AND " +
           "(:equipmentId IS NULL OR p.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR p.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:beforeDate IS NULL OR p.purchaseDate < :beforeDate)")
    long sumQuantityBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("beforeDate") LocalDate beforeDate
    );

    @Query("SELECT COALESCE(SUM(p.quantity), 0) FROM Purchase p WHERE " +
           "(:baseId IS NULL OR p.base.id = :baseId) AND " +
           "(:equipmentId IS NULL OR p.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR p.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR p.purchaseDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.purchaseDate <= :endDate)")
    long sumQuantityBetweenDates(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByReferenceNumber(String referenceNumber);
}
