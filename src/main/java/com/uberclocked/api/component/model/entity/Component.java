package com.uberclocked.api.component.model.entity;

import java.util.HashSet;
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
  private Set<ComponentField> fields = new HashSet<>();

  protected Component() {
  }

  public Component(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }

  public Set<ComponentField> fields() {
    return Set.copyOf(fields);
  }

  public String code() {
    return code;
  }

  public String displayName() {
    return displayName;
  }

  public ComponentField removeField(ComponentField field) {
    fields.remove(field);
    return field;
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
