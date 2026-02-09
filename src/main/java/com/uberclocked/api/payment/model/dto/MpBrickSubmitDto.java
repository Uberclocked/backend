package com.uberclocked.api.payment.model.dto;

import java.util.UUID;

public record MpBrickSubmitDto(
    UUID orderId,
    String token,
    String paymentMethodId,
    String issuerId,
    Integer installments,
    PayerDto payer) {
}
