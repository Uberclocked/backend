package com.uberclocked.api.promotion.repository;

import com.uberclocked.api.promotion.model.entity.Promotion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Promotion> findByCodeIgnoreCase(String code);
}

