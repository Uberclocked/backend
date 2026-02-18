package com.uberclocked.api.wheel.repository;

import com.uberclocked.api.wheel.model.entity.WheelSpinEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WheelSpinRepository extends JpaRepository<WheelSpinEntity, Long> {
    Optional<WheelSpinEntity> findByUserIdAndSpinDate(String userId, LocalDate spinDate);
}