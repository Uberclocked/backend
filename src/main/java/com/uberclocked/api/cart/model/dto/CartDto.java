package com.uberclocked.api.cart.model.dto;

import com.uberclocked.api.promotion.model.dto.PromotionLiteDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CartDto(
        UUID id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String status,
        List<CartItemDto> items,
        PromotionLiteDto appliedPromotion,
        Double discountAmount
) {}