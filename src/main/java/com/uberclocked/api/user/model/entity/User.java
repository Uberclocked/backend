package com.uberclocked.api.user.model.entity;

import com.uberclocked.api.cart.model.entity.Cart;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Setter
  private UUID id;

  @Column(nullable = false, unique = true, updatable = false)
  private String auth0Id;

  @Setter
  @Column(nullable = false, unique = true)
  private String userName;

  @Setter
  @Column(nullable = false, unique = true)
  private String email;

  @Setter private LocalDateTime lastLogin;

  @Setter private String country;

  @Setter private String cellPhone;

  @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Cart> carts;

  @Setter
  @Enumerated(EnumType.STRING)
  private UserStatus userStatus;

  public User() {}

  public User(String subject, String userName, String email) {
    this.auth0Id = subject;
    this.userName = userName;
    this.email = email;
  }
}
