package com.uberclocked.api.reviews.controller;

import com.uberclocked.api.reviews.model.dto.CreateReviewDto;
import com.uberclocked.api.reviews.model.dto.ModifyReviewDataDto;
import com.uberclocked.api.reviews.model.entity.Review;
import com.uberclocked.api.reviews.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewsController {

    private final ReviewService reviewService;

    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(
            @RequestBody CreateReviewDto reviewDto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return reviewService.createReview(reviewDto, jwt);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable UUID id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/product/{productId}")
    public List<Review> getReviewsByProduct(@PathVariable UUID productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    @PatchMapping("/{id}")
    public Review updateReview(
            @PathVariable UUID id,
            @RequestBody ModifyReviewDataDto dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return reviewService.updateReview(id, dto, jwt);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        reviewService.deleteReview(id, jwt);
    }
}
