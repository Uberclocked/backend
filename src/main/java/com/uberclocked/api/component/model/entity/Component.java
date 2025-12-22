package com.uberclocked.api.component.model.entity;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.uberclocked.api.component.model.entity.type.ComponentType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "component")
public class Component {
  @Id
  @GeneratedValue
  private Long id;

  @Version
  private long version;

  @ManyToOne(optional = false)
  @JoinColumn(name = "type_code", nullable = false)
  private ComponentType type;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(nullable = false)
  private Map<String, Object> values = new HashMap<>();

  protected Component() {
  }

  public Component(ComponentType type) {
    this.type = type;
  }

  public Long id() {
    return id;
  }

  public ComponentType type() {
    return type;
  }

  public Map<String, Object> values() {
    return Map.copyOf(values);
  }

  public void setValue(String fieldName, Object value) {
    values.put(fieldName, value);
  }
}
