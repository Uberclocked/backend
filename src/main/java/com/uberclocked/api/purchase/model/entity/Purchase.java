package com.uberclocked.api.purchase.model.entity;

import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "purchase")
@Getter
@Setter
public class Purchase {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(optional = false)
  private User user;

  @OneToMany private List<CartItem> items;

  @Enumerated(EnumType.STRING)
  private PurchaseStatus status;

  private Double totalAmount;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private LocalDateTime pickupDate;
}
