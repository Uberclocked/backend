package com.uberclocked.api.review.repository;

import com.uberclocked.api.review.model.entity.Review;
import com.uberclocked.api.user.model.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

  //    List<Review> findByProduct(Product product);

  //    boolean existsByUserAndProduct(User user, Product product);
  boolean existsByUser(User user);
}
