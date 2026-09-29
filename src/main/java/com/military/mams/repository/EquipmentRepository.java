package com.military.mams.repository;

import com.military.mams.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByCode(String code);
    boolean existsByCode(String code);
    List<Equipment> findByEquipmentTypeId(Long equipmentTypeId);
}
