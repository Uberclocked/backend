package com.uberclocked.api.review.controller;

import com.uberclocked.api.review.mapper.ReviewMapper;
import com.uberclocked.api.review.model.dto.CreateReviewDto;
import com.uberclocked.api.review.model.dto.ModifyReviewDataDto;
import com.uberclocked.api.review.model.dto.ProductRatingDto;
import com.uberclocked.api.review.model.dto.ReviewResponseDto;
import com.uberclocked.api.review.model.entity.Review;
import com.uberclocked.api.review.service.ReviewService;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reviews")
@Validated
public class ReviewsController {

  private final ReviewService reviewService;
  private final ReviewMapper reviewMapper;

  public ReviewsController(ReviewService reviewService, ReviewMapper reviewMapper) {
    this.reviewService = reviewService;
    this.reviewMapper = reviewMapper;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ReviewResponseDto create(@Valid @RequestBody CreateReviewDto dto, @AuthenticationPrincipal Jwt jwt) {
    return reviewMapper.toDto(reviewService.createReview(dto, jwt));
  }

  @GetMapping("/product/{skuPrefix}")
  public List<ReviewResponseDto> byProduct(@PathVariable String skuPrefix) {
    return reviewMapper.toDtoList(reviewService.getByProduct(skuPrefix));
  }

  @GetMapping("/product/{skuPrefix}/rating")
  public ProductRatingDto rating(@PathVariable String skuPrefix) {
    double avg = reviewService.getAvgRating(skuPrefix);
    long count = reviewService.getCount(skuPrefix);
    return new ProductRatingDto(avg, count);
  }

  @GetMapping("/me")
  public List<ReviewResponseDto> myReviews(@AuthenticationPrincipal Jwt jwt) {
    return reviewMapper.toDtoList(reviewService.getMyReviews(jwt));
  }

  @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
  public Review getReviewById(@PathVariable UUID id) {
    return reviewService.getReviewById(id);
  }

  @PatchMapping("/{id}")
  public ReviewResponseDto update(@PathVariable UUID id, @RequestBody ModifyReviewDataDto dto, @AuthenticationPrincipal Jwt jwt) {
    return reviewMapper.toDto(reviewService.update(id, dto, jwt));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReview(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    reviewService.deleteReview(id, jwt);
  }

  @GetMapping
  @PreAuthorize("hasRole('Admin')")
  public List<ReviewResponseDto> allReviews(
          @RequestParam(required = false) String skuPrefix
  ) {
    List<Review> list = (skuPrefix == null || skuPrefix.isBlank())
            ? reviewService.getAllReviews()
            : reviewService.getByProduct(skuPrefix);

    return reviewMapper.toDtoList(list);
  }
}
