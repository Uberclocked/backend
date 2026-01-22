package com.uberclocked.api.reviews.repository;

import com.uberclocked.api.reviews.model.entity.Review;
import com.uberclocked.api.users.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

//    List<Review> findByProduct(Product product);

//    boolean existsByUserAndProduct(User user, Product product);
    boolean existsByUser(User user);
}