package com.uberclocked.api.component.model.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.uberclocked.api.component.model.entity.field.FieldType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "component_type")
public class Component {
  @Id
  @Column(nullable = false, updatable = false)
  private String code;

  @Column(nullable = false)
  private String displayName;

  @ElementCollection
  @CollectionTable(name = "component_field", joinColumns = @JoinColumn(name = "component_code"))
  private Set<ComponentField> fields;

  protected Component() {
    this.fields = new HashSet<>();
  }

  public Component(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
    this.fields = new HashSet<>();
  }

  public Set<ComponentField> getFields() {
    return fields;
  }

  public String getCode() {
    return code;
  }

  public String getDisplayName() {
    return displayName;
  }

  public ComponentField removeField(String fieldName) {
    Iterator<ComponentField> iterator = fields.iterator();

    while (iterator.hasNext()) {
      ComponentField field = iterator.next();
      if (field.name().equals(fieldName)) {
        iterator.remove();
        return field;
      }
    }

    throw new NoSuchElementException(
        "Field with name '%s' not found".formatted(fieldName));
  }

  public ComponentField addField(
      String name,
      FieldType type,
      boolean required,
      String defaultValue) {
    ComponentField field = new ComponentField(name, type, required, defaultValue);
    this.fields.add(field);
    return field;
  }
}
