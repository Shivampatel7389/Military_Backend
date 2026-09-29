package com.military.mams.repository;

import com.military.mams.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    @Query("SELECT e FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR e.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:equipmentId IS NULL OR e.equipment.id = :equipmentId) AND " +
           "(:reason IS NULL OR e.reason = :reason) AND " +
           "(:startDate IS NULL OR e.expenditureDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenditureDate <= :endDate) " +
           "ORDER BY e.expenditureDate DESC, e.id DESC")
    List<Expenditure> findWithFilters(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("reason") String reason,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentId IS NULL OR e.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR e.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:beforeDate IS NULL OR e.expenditureDate < :beforeDate)")
    long sumExpendedBeforeDate(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("beforeDate") LocalDate beforeDate
    );

    @Query("SELECT COALESCE(SUM(e.quantity), 0) FROM Expenditure e WHERE " +
           "(:baseId IS NULL OR e.base.id = :baseId) AND " +
           "(:equipmentId IS NULL OR e.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR e.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR e.expenditureDate >= :startDate) AND " +
           "(:endDate IS NULL OR e.expenditureDate <= :endDate)")
    long sumExpendedBetweenDates(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByExpenditureCode(String expenditureCode);
}
