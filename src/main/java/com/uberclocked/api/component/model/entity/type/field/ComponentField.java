package com.uberclocked.api.component.model.entity.type.field;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.uberclocked.api.component.model.entity.type.ComponentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "component_field", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "component_type_code", "name" })
})
public class ComponentField {

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private FieldType type;

  @Column(nullable = false)
  private boolean required;

  @JdbcTypeCode(SqlTypes.JSON)
  private Object defaultValue;

  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> constraints;

  @ManyToOne(optional = false)
  @JoinColumn(name = "component_type_code")
  private ComponentType componentType;

  protected ComponentField() {
  }

  public ComponentField(
      String name,
      FieldType type,
      boolean required,
      Object defaultValue,
      Map<String, Object> constraints) {
    this.name = name;
    this.type = type;
    this.required = required;
    this.defaultValue = defaultValue;
    this.constraints = constraints;
  }

  public void setComponentType(ComponentType componentType) {
    this.componentType = componentType;
  }
}
