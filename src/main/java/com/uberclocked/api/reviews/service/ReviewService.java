package com.uberclocked.api.reviews.service;

import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.reviews.model.dto.CreateReviewDto;
import com.uberclocked.api.reviews.model.dto.ModifyReviewDataDto;
import com.uberclocked.api.reviews.model.entity.Review;
import com.uberclocked.api.reviews.repository.ReviewRepository;
import com.uberclocked.api.users.model.entity.User;
import com.uberclocked.api.users.service.UsersService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final UsersService usersService;

  //    private final ProductRepository productRepository;

  public ReviewService(ReviewRepository reviewRepository, UsersService usersService
      //            ProductRepository productRepository
      ) {
    this.reviewRepository = reviewRepository;
    this.usersService = usersService;
    //        this.productRepository = productRepository;
  }

  public Review createReview(CreateReviewDto reviewDto, Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    //        Product product = productService.getOrCreate(reviewDto.productId)

    //        if (reviewRepository.existsByUserAndProduct(user, product)) {
    if (reviewRepository.existsByUser(user)) {
      throw new IllegalStateException("Already reviewed this product");
    }
    Review review = new Review();
    review.setUser(user);
    //        review.setProduct(product);
    review.setQualification(reviewDto.qualification());
    review.setMessage(review.getMessage());
    review.setCreation(LocalDateTime.now());
    return reviewRepository.save(review);
  }

  public List<Review> getReviewsByProduct(UUID productId) {

    //        Product product = productRepository.getProduct(productId);

    //        return reviewRepository.findByProduct(product);
    return new ArrayList<>();
  }

  public Review getReviewById(UUID id) {
    return reviewRepository
        .findById(id)
        .orElseThrow(() -> new ResourceDoesNotExistsException("Review not found"));
  }

  public void deleteReview(UUID reviewId, Jwt jwt) {

    Review review = getReview(reviewId);

    User user = usersService.getUserOrCreate(jwt);

    List<String> roles = jwt.getClaimAsStringList("https://uberclocked.com/roles");

    boolean isAdmin = roles != null && roles.contains("ADMIN");
    boolean isOwner = review.getUser().getId().equals(user.getId());

    if (!isAdmin && !isOwner) {
      throw new SecurityException("Not have permissions for delete this review");
    }

    reviewRepository.delete(review);
  }

  public Review updateReview(UUID reviewId, ModifyReviewDataDto dto, Jwt jwt) {
    Review review = getReview(reviewId);

    User user = usersService.getUserOrCreate(jwt);
    boolean isOwner = review.getUser().getId().equals(user.getId());
    if (!isOwner) {
      throw new SecurityException("Not have permissions to modify this review");
    }
    if (dto.qualification() != null) {
      review.setQualification(dto.qualification());
    }
    if (dto.message() != null && !dto.message().isBlank()) {
      review.setMessage(dto.message());
    }
    return reviewRepository.save(review);
  }

  private @NonNull Review getReview(UUID reviewId) {
    return reviewRepository
        .findById(reviewId)
        .orElseThrow(() -> new ResourceDoesNotExistsException("Review not found"));
  }
}
