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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cart_item")
@Getter
public class CartItem {

  @Id @GeneratedValue private UUID id;

  @Setter private String name;

  @ManyToOne @Setter private Cart cart;

  @ManyToOne @Setter private Product product;

  @Setter private Integer quantity;
  @Setter private double totalPrice;

  @ElementCollection
  @CollectionTable(name = "cart_item_components", joinColumns = @JoinColumn(name = "cart_item_id"))
  @MapKeyColumn(name = "component_type")
  @Column(name = "product_sku")
  @Setter
  private Map<String, String> components = new HashMap<>();
}
