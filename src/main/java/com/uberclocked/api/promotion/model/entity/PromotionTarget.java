package com.uberclocked.api.promotion.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "promotion_targets",
        indexes = {
                @Index(name = "idx_promo_target_promo", columnList = "promotion_id")
        })
public class PromotionTarget {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetKind kind;

    private String sku;

    @Column(length = 32)
    private String componentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetMode mode = TargetMode.INCLUDE;

    public enum TargetKind {
        PRODUCT_SKU,
        COMPONENT_TYPE,
        COMPONENT_SKU
    }

    public enum TargetMode {
        INCLUDE,
        EXCLUDE
    }
}
