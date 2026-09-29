package com.military.mams.repository;

import com.military.mams.entity.Base;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseRepository extends JpaRepository<Base, Long> {
    Optional<Base> findByCode(String code);
    boolean existsByCode(String code);
}
