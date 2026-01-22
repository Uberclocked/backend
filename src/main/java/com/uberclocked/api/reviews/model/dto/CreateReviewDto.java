package com.uberclocked.api.reviews.model.dto;

import java.util.UUID;

public record CreateReviewDto(UUID productId, Integer qualification, String message) {}
