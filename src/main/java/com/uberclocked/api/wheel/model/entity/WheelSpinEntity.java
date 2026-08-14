package com.uberclocked.api.wheel.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "wheel_spins",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "spin_date"}))
public class WheelSpinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false, length=128)
    private String userId;

    @Column(name="spin_date", nullable=false)
    private LocalDate spinDate;

    @Column(name="promotion_id", length=64)
    private UUID promotionId;

    @Column(name="prize_label", nullable=false, length=128)
    private String prizeLabel;

    @Column(name="discount", nullable=false)
    private Integer discount;

    @Column(name="created_at", nullable=false)
    private Instant createdAt = Instant.now();

}
