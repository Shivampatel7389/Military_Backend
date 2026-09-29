package com.military.mams.repository;

import com.military.mams.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    @Query("SELECT t FROM Transfer t WHERE " +
           "(:baseId IS NULL OR t.fromBase.id = :baseId OR t.toBase.id = :baseId) AND " +
           "(:fromBaseId IS NULL OR t.fromBase.id = :fromBaseId) AND " +
           "(:toBaseId IS NULL OR t.toBase.id = :toBaseId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:equipmentId IS NULL OR t.equipment.id = :equipmentId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) " +
           "ORDER BY t.transferDate DESC, t.id DESC")
    List<Transfer> findWithFilters(
            @Param("baseId") Long baseId,
            @Param("fromBaseId") Long fromBaseId,
            @Param("toBaseId") Long toBaseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Transfers In to destination base before date
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "t.toBase.id = :baseId AND " +
           "(:equipmentId IS NULL OR t.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "t.status = 'COMPLETED' AND " +
           "(:beforeDate IS NULL OR t.transferDate < :beforeDate)")
    long sumTransfersInBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("beforeDate") LocalDate beforeDate
    );

    // Transfers Out from source base before date
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "t.fromBase.id = :baseId AND " +
           "(:equipmentId IS NULL OR t.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "t.status = 'COMPLETED' AND " +
           "(:beforeDate IS NULL OR t.transferDate < :beforeDate)")
    long sumTransfersOutBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("beforeDate") LocalDate beforeDate
    );

    // Transfers In to destination base between dates
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "t.toBase.id = :baseId AND " +
           "(:equipmentId IS NULL OR t.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "t.status = 'COMPLETED' AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate)")
    long sumTransfersInBetweenDates(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Transfers Out from source base between dates
    @Query("SELECT COALESCE(SUM(t.quantity), 0) FROM Transfer t WHERE " +
           "t.fromBase.id = :baseId AND " +
           "(:equipmentId IS NULL OR t.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR t.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "t.status = 'COMPLETED' AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate)")
    long sumTransfersOutBetweenDates(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transfer t WHERE " +
           "t.toBase.id = :baseId AND " +
           "(:equipmentTypeId IS NULL OR t.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:equipmentId IS NULL OR t.equipment.id = :equipmentId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) AND " +
           "t.status = 'COMPLETED' ORDER BY t.transferDate DESC")
    List<Transfer> findTransfersIn(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT t FROM Transfer t WHERE " +
           "t.fromBase.id = :baseId AND " +
           "(:equipmentTypeId IS NULL OR t.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:equipmentId IS NULL OR t.equipment.id = :equipmentId) AND " +
           "(:startDate IS NULL OR t.transferDate >= :startDate) AND " +
           "(:endDate IS NULL OR t.transferDate <= :endDate) AND " +
           "t.status = 'COMPLETED' ORDER BY t.transferDate DESC")
    List<Transfer> findTransfersOut(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByReferenceNumber(String referenceNumber);
}
