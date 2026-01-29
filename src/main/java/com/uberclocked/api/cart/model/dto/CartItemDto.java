package com.uberclocked.api.cart.model.dto;

import java.util.Map;
import java.util.UUID;

public record CartItemDto(
        UUID id,
        String name,
        byte[] image,
        Integer quantity,
        double totalPrice,
        String productSku,
        String productName,
        Map<String, String> components
) {}
