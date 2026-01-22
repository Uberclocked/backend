package com.uberclocked.api.reviews.model.entity;

import com.uberclocked.api.users.model.entity.User;
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
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id"})})
@Getter
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID reviewId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  @Setter
  private User user;

  //    @ManyToOne
  //    @JoinColumn(name = "product_id", nullable = false)
  //    @Setter
  //    private Product product;

  @NotNull
  @Min(1)
  @Max(5)
  @Column(nullable = false)
  @Setter
  private Integer qualification;

  @Column(length = 1000)
  @Setter
  private String message;

  @Setter private LocalDateTime creation;
}
