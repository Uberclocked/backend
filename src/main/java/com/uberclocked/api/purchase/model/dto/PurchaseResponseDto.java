package com.uberclocked.api.purchase.model.dto;

import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.purchase.model.entity.PurchaseStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseResponseDto(
        UUID id,
        PurchaseStatus status,
        Double totalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime pickupDate,
        UUID cartId,
        List<CartItemDto> items
) {}