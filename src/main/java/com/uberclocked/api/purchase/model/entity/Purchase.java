package com.uberclocked.api.purchase.model.entity;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase")
@Getter
@Setter
public class Purchase {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(optional = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = true)
  private User user;

  @OneToOne(optional = false, fetch = FetchType.EAGER)
  @JoinColumn(name = "cart_id", nullable = false, unique = true)
  private Cart cart;

  @Enumerated(EnumType.STRING)
  private PurchaseStatus status;

  private Double totalAmount;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private LocalDateTime pickupDate;
}
