package com.uberclocked.api.market.model.dto;

public record PostDataDto(
    String title,
    String description,
    Double price,
    String category) {
}
