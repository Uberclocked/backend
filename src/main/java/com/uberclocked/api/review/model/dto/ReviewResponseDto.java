package com.uberclocked.api.review.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponseDto(
        UUID id,
        String skuPrefix,
        UUID userId,
        String userName,
        Integer qualification,
        String message,
        LocalDateTime createdAt
) {}
