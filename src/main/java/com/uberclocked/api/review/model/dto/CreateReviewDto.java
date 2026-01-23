package com.uberclocked.api.review.model.dto;

import java.util.UUID;

public record CreateReviewDto(UUID productId, Integer qualification, String message) {}
