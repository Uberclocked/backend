package com.uberclocked.api.cart.model.entity;

import com.uberclocked.api.product.model.entity.Product;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "cart_item")
@Getter
public class CartItem {

  @Id
  @GeneratedValue
  private UUID id;

  public UUID id() {
    return id;
  }

  @Setter
  private String name;

  public String name() {
    return name;
  }

  @ManyToOne
  @Setter
  private Cart cart;

  @ManyToOne
  @Setter
  private Product product;

  @Setter
  private Integer quantity;

  public Integer quantity() {
    return quantity;
  }

  @Setter
  private double totalPrice;

  public double totalPrice() {
    return totalPrice;
  }

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  @Setter
  private LocalDateTime createdAt;

  @ElementCollection
  @CollectionTable(name = "cart_item_components", joinColumns = @JoinColumn(name = "cart_item_id"))
  @MapKeyColumn(name = "component_type")
  @Column(name = "product_sku")
  @Setter
  private Map<String, String> components = new HashMap<>();
}
