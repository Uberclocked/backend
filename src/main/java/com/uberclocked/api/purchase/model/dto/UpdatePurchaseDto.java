package com.uberclocked.api.purchase.model.dto;

import com.uberclocked.api.purchase.model.entity.PurchaseStatus;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;


public record UpdatePurchaseDto(
        PurchaseStatus status,
        @FutureOrPresent LocalDateTime pickupDate
) {}