package com.uberclocked.api.component.model.entity.type;

import java.util.HashSet;
import java.util.Set;

import com.uberclocked.api.component.model.entity.type.field.ComponentField;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "component_type")
public class ComponentType {

  @Id
  @Column(nullable = false, updatable = false)
  private String code;

  @Column(nullable = false)
  private String displayName;

  @OneToMany(mappedBy = "componentType", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<ComponentField> fields = new HashSet<>();

  protected ComponentType() {
  }

  public ComponentType(String code, String displayName) {
    this.code = code;
    this.displayName = displayName;
  }

  public void addField(ComponentField field) {
    field.setComponentType(this);
    fields.add(field);
  }

  public void removeField(ComponentField field) {
    fields.remove(field);
    field.setComponentType(null);
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
}
