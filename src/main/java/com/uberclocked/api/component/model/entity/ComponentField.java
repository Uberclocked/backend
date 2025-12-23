package com.uberclocked.api.component.model.entity;

import com.uberclocked.api.component.model.entity.field.FieldType;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public record ComponentField(String name, FieldType type, boolean required, String defaultValue) {
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ComponentField other)) return false;
    return Objects.equals(this.name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
