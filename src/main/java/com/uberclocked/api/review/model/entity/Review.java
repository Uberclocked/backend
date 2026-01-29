package com.uberclocked.api.review.model.entity;

import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "product_id"})}
)
@Getter
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @Setter
  private User user;

  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  @Setter
  private Product product;

  @NotNull
  @Min(1)
  @Max(5)
  @Column(nullable = false)
  @Setter
  private Integer qualification;

  @Column(length = 1000)
  @Setter
  private String message;

  @Column(nullable = false)
  @Setter
  private LocalDateTime createdAt;
}