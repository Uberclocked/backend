package com.uberclocked.api.product.model.entity;

import com.uberclocked.api.component.model.entity.Component;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Product {

  @Id
  @Column(nullable = false, updatable = false)
  private String skuPrefix;

  @Column(nullable = false)
  @Setter
  private String name;

  @Column(nullable = false)
  @Setter
  private String image;

  @ManyToOne(optional = false)
  @JoinColumn(name = "component_sku_prefix")
  @Setter
  private Component component;

  @Column(nullable = false)
  @Setter
  private double price;

  @Column(nullable = false)
  @Setter
  private int stock;

  @Column(nullable = false)
  @Setter
  private boolean active = true;

  @ElementCollection
  @CollectionTable(name = "product_attribute", joinColumns = @JoinColumn(name = "product_sku"))
  @MapKeyColumn(name = "field_name")
  @Column(name = "field_value")
  @Setter
  private Map<String, String> attributes = new HashMap<>();

  public Product() {}

  public Product(String skuPrefix, String name, Component component, double price, int stock) {
    this.skuPrefix = skuPrefix;
    this.name = name;
    this.component = component;
    this.price = price;
    this.stock = stock;
  }
}
