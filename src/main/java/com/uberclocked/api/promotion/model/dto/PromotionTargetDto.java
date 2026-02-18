package com.uberclocked.api.promotion.model.dto;

import com.uberclocked.api.promotion.model.entity.PromotionTarget;

public record PromotionTargetDto(
        Long id,
        PromotionTarget.TargetKind kind,
        String sku,
        String componentType,
        PromotionTarget.TargetMode mode
) {}