package com.uberclocked.api.promotion.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PromotionDto(
        UUID id,
        String code,
        String title,
        String description,
        Integer discount,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Boolean active,
        Integer maxUses,
        Integer usedCount,
        UUID userId,
        UUID companyId,
        List<PromotionTargetDto> targets
) {}