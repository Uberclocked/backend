package com.uberclocked.api.market.model.entity;

import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class PostInterest {

  @Id private UUID id;

  @ManyToOne @Setter private Post post;

  @ManyToOne @Setter private User interested;

  @Setter private boolean infoPurchased;

  private LocalDateTime createdAt;

  public PostInterest() {}

  public PostInterest(Post post, User interested) {
    this.id = UUID.randomUUID();
    this.post = post;
    this.interested = interested;
    this.infoPurchased = false;
    this.createdAt = LocalDateTime.now();
  }
}
