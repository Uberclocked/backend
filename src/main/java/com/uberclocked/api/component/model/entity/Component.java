package com.uberclocked.api.component.model.entity;

import com.uberclocked.api.component.model.entity.field.FieldType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Entity
@Table(name = "component")
public class Component {
  @Id
  @Column(nullable = false, updatable = false)
  private String skuPrefix;

  @Column(nullable = false)
  private String displayName;

  @ElementCollection
  @CollectionTable(
      name = "component_field",
      joinColumns = @JoinColumn(name = "component_sku_prefix"))
  @MapKeyColumn(name = "field_name")
  private Map<String, ComponentField> fields;

  protected Component() {
    this.fields = new HashMap<>();
  }

  public Component(String skuPrefix, String displayName) {
    this.skuPrefix = skuPrefix;
    this.displayName = displayName;
    this.fields = new HashMap<>();
  }

  public HashMap<String, ComponentField> getFields() {
    return new HashMap<>(fields);
  }

  public String getSkuPrefix() {
    return skuPrefix;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public ComponentField removeField(String fieldName) {
    if (!fields.containsKey(fieldName)) {
      throw new NoSuchElementException("Field with name '%s' not found".formatted(fieldName));
    }
    return fields.remove(fieldName);
  }

  public ComponentField addField(
      String name, FieldType type, boolean required, String defaultValue) {
    if (fields.containsKey(name)) {
      throw new IllegalArgumentException("Field with name '%s' already exists".formatted(name));
    }
    ComponentField field = new ComponentField(type, required, defaultValue);
    fields.put(name, field);
    return field;
  }

  public void clearFields() {
    this.fields = new HashMap<>();
  }
}
