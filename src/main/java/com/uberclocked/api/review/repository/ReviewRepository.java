package com.uberclocked.api.review.repository;

import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.review.model.entity.Review;
import com.uberclocked.api.user.model.entity.User;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

  boolean existsByUserAndProduct(User user, Product product);

  List<Review> findByProductOrderByCreatedAtDesc(Product product);

  List<Review> findByUserOrderByCreatedAtDesc(User user);

  long countByProduct(Product product);

  @Query("select coalesce(avg(r.qualification),0) from Review r where r.product = :product")
  double avgByProduct(Product product);

  List<Review> findAllByOrderByCreatedAtDesc();
}