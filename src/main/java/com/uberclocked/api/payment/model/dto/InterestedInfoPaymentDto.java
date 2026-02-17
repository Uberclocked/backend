package com.uberclocked.api.payment.model.dto;

import java.util.UUID;

public record InterestedInfoPaymentDto(
        UUID postId,
        UUID interestedUserId,
        String token,
        String paymentMethodId,
        String issuerId,
        Integer installments,
        PayerDto payer
) {}