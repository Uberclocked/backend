package com.uberclocked.api.review.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ModifyReviewDataDto(String message, @Min(1) @Max(5) Integer qualification) {}
