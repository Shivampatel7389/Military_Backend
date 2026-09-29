package com.military.mams.repository;

import com.military.mams.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    Optional<Personnel> findByServiceNumber(String serviceNumber);
    boolean existsByServiceNumber(String serviceNumber);
    List<Personnel> findByBaseId(Long baseId);
}
