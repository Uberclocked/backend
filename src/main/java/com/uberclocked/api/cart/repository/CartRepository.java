package com.uberclocked.api.cart.repository;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartStatus;
import com.uberclocked.api.user.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
  Optional<Cart> findByUserAndStatus(User user, CartStatus status);
}
