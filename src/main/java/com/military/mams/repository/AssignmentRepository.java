package com.military.mams.repository;

import com.military.mams.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Query("SELECT a FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentTypeId IS NULL OR a.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:equipmentId IS NULL OR a.equipment.id = :equipmentId) AND " +
           "(:personnelId IS NULL OR a.personnel.id = :personnelId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:startDate IS NULL OR a.assignmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.assignmentDate <= :endDate) " +
           "ORDER BY a.assignmentDate DESC, a.id DESC")
    List<Assignment> findWithFilters(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("personnelId") Long personnelId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentId IS NULL OR a.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR a.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "a.status = 'ACTIVE'")
    long sumActiveAssignedQuantity(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId
    );

    @Query("SELECT COALESCE(SUM(a.quantity), 0) FROM Assignment a WHERE " +
           "(:baseId IS NULL OR a.base.id = :baseId) AND " +
           "(:equipmentId IS NULL OR a.equipment.id = :equipmentId) AND " +
           "(:equipmentTypeId IS NULL OR a.equipment.equipmentType.id = :equipmentTypeId) AND " +
           "(:startDate IS NULL OR a.assignmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR a.assignmentDate <= :endDate)")
    long sumQuantityAssignedBetweenDates(
            @Param("baseId") Long baseId,
            @Param("equipmentTypeId") Long equipmentTypeId,
            @Param("equipmentId") Long equipmentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByAssignmentCode(String assignmentCode);
}
