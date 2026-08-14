package com.uberclocked.api.market.model.entity;

import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Setter
  private String title;

  @Setter
  private byte[] image;

  @Column(length = 2000)
  @Setter
  private String description;

  @Setter private Double price;

  @Setter private String category;

  @Enumerated(EnumType.STRING)
  @Setter
  private PostStatus status;

  private LocalDateTime createdAt;

  @ManyToOne private User seller;

  public Post() {}

  public Post(
      String title,
      byte[] image,
      String description,
      Double price,
      String category,
      User seller,
      LocalDateTime createdAt) {
    this.title = title;
    this.image = image;
    this.description = description;
    this.price = price;
    this.category = category;
    this.seller = seller;
    this.createdAt = createdAt;
  }
}
