package com.uberclocked.api.payment.model.dto;

public record PayerDto(
    String email,
    IdentificationDto identification) {
}
