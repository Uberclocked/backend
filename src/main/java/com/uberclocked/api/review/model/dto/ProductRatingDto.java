package com.uberclocked.api.review.model.dto;

public record ProductRatingDto(
        double avgRating,
        long count
) {}
