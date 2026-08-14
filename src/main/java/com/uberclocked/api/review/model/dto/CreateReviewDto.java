package com.uberclocked.api.review.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateReviewDto(
    @NotNull String skuPrefix,
    @NotNull @Min(1) @Max(5) Integer qualification,
    String message) {
}
