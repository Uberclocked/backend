package com.uberclocked.api.product.model.entity;

import com.uberclocked.api.component.model.entity.Component;
import com.uberclocked.api.component.model.entity.ComponentField;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
public class Product {

  @Id
  @Column(nullable = false, updatable = false)
  @Setter
  private String skuPrefix;

  @Column(nullable = false)
  @Setter
  private String name;

  @Lob
  @JdbcTypeCode(SqlTypes.BINARY)
  @Column(name = "image")
  private byte[] image;

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
  private Map<String, String> attributes = new HashMap<>();

  public Product() {
  }

  public Product(String skuPrefix, String name, Component component, double price, int stock) {
    this.skuPrefix = skuPrefix;
    this.name = name;
    this.component = component;
    this.price = price;
    this.stock = stock;
  }

  public void initializeAttributesFromComponent(
      Map<String, String> providedAttributes) {
    Map<String, ComponentField> fields = component.getFields();

    for (Map.Entry<String, ComponentField> entry : fields.entrySet()) {
      String fieldName = entry.getKey();
      ComponentField field = entry.getValue();

      if (providedAttributes != null && providedAttributes.containsKey(fieldName)) {
        attributes.put(fieldName, providedAttributes.get(fieldName));
        continue;
      }

      if (field.required()) {
        if (field.defaultValue() != null) {
          attributes.put(fieldName, field.defaultValue());
        } else {
          throw new IllegalArgumentException(
              "Missing required field '%s' for component '%s'"
                  .formatted(fieldName, component.getSkuPrefix()));
        }
      }
    }
  }

  public void updateAttribute(String name, String value) {
    if (!component.getFields().containsKey(name)) {
      throw new IllegalArgumentException(
          "Field '%s' is not defined in component '%s'"
              .formatted(name, component.getSkuPrefix()));
    }
    attributes.put(name, value);
  }

  private static final int MAX_IMAGE_SIZE_BYTES = 400 * 1024;

  public void setImage(byte[] imageBytes) {
    if (imageBytes != null && imageBytes.length > MAX_IMAGE_SIZE_BYTES) {
      throw new IllegalArgumentException("Image exceeds max size of 400 KB");
    }
    this.image = imageBytes;
  }

  public void clearAttributes() {
    this.attributes.clear();
  }
}
