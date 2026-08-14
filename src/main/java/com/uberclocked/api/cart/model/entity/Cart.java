package com.uberclocked.api.cart.model.entity;

import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart")
@Getter
public class Cart {
  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne
  @Setter
  private User user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Setter
  @OrderBy("createdAt ASC")
  private List<CartItem> items = new ArrayList<>();

  @Setter
  private LocalDateTime createdAt;
  @Setter
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  @Setter
  private CartStatus status;

  public List<CartItem> items() {
    return new ArrayList<>(items);
  }

  @ManyToOne
  @Setter
  private Promotion appliedPromotion;

  @Setter
  private Double discountAmount;
}
