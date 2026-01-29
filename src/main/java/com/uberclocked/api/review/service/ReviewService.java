package com.uberclocked.api.review.service;

import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.service.ProductService;
import com.uberclocked.api.review.model.dto.CreateReviewDto;
import com.uberclocked.api.review.model.dto.ModifyReviewDataDto;
import com.uberclocked.api.review.model.entity.Review;
import com.uberclocked.api.review.repository.ReviewRepository;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final UsersService usersService;
  private final ProductService productService;

  public ReviewService(ReviewRepository reviewRepository, UsersService usersService,ProductService productService
      ) {
    this.reviewRepository = reviewRepository;
    this.usersService = usersService;
    this.productService = productService;
  }

  public Review createReview(CreateReviewDto dto, Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);

    Product product =
            productService.getById(dto.skuPrefix());
    if (reviewRepository.existsByUserAndProduct(user, product)) {
      throw new IllegalStateException("Already reviewed this product");
    }
    Review review = new Review();
    review.setUser(user);
    review.setProduct(product);
    review.setQualification(dto.qualification());
    review.setMessage(dto.message());
    review.setCreatedAt(LocalDateTime.now());

    return reviewRepository.save(review);
  }

  public List<Review> getAllReviews() {
    return reviewRepository.findAllByOrderByCreatedAtDesc();
  }

  public List<Review> getByProduct(String productId) {
    Product product =
            productService.
                    getById(productId);
    return reviewRepository.findByProductOrderByCreatedAtDesc(product);
  }

  public Review getReviewById(UUID id) {
    return reviewRepository
        .findById(id)
        .orElseThrow(() -> new ResourceDoesNotExistsException("Review not found"));
  }

  public double getAvgRating(String productId) {
    Product product =
            productService.
                    getById(productId);
    return reviewRepository.avgByProduct(product);
  }
  public long getCount(String productId) {
    Product product =
            productService.
                    getById(productId);
    return reviewRepository.countByProduct(product);
  }

  public void deleteReview(UUID reviewId, Jwt jwt) {

    Review review = getReview(reviewId);

    User user = usersService.getUserOrCreate(jwt);

    List<String> roles = jwt.getClaimAsStringList("https://uberclocked.com/roles");

    boolean isAdmin = roles != null && roles.contains("Admin");
    boolean isOwner = review.getUser().getId().equals(user.getId());

    if (!isAdmin && !isOwner) {
      throw new SecurityException("Not have permissions for delete this review");
    }
    reviewRepository.delete(review);
  }

  public Review update(UUID reviewId, ModifyReviewDataDto dto, Jwt jwt) {
    Review review = getReviewById(reviewId);
    User user = usersService.getUserOrCreate(jwt);

    boolean isOwner = review.getUser().getId().equals(user.getId());
    if (!isOwner) throw new SecurityException("Not allowed");

    if (dto.qualification() != null) review.setQualification(dto.qualification());
    if (dto.message() != null && !dto.message().isBlank()) review.setMessage(dto.message());

    return reviewRepository.save(review);
  }

  public List<Review> getMyReviews(Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    return reviewRepository.findByUserOrderByCreatedAtDesc(user);
  }

  private @NonNull Review getReview(UUID reviewId) {
    return reviewRepository
        .findById(reviewId)
        .orElseThrow(() -> new ResourceDoesNotExistsException("Review not found"));
  }
}
