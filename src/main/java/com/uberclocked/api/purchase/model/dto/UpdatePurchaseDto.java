package com.uberclocked.api.purchase.model.dto;

import com.uberclocked.api.purchase.model.entity.PurchaseStatus;
import java.time.LocalDateTime;

public record UpdatePurchaseDto(PurchaseStatus status, LocalDateTime pickupDate) {}
