package com.uberclocked.api.promotion.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PromotionLiteDto(
        UUID id,
        String code,
        Integer discount,
        String title,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}