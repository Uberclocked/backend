package com.uberclocked.api.payment.model.dto;

import java.util.UUID;

public record PaymentDto(UUID transaction_id, Long payment_id, PaymentStatus status) {
}
