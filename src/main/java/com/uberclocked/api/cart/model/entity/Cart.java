package com.uberclocked.api.cart.model.entity;

import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.CascadeType;
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

import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "cart",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "status"})
        }
)@Getter
public class Cart {
  @Id @GeneratedValue private UUID id;

  @ManyToOne @Setter private User user;

  @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
  @Setter
  private List<CartItem> items;

  @Setter private LocalDateTime createdAt;
  @Setter private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  @Setter
  private CartStatus status;
}
