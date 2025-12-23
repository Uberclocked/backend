package com.uberclocked.api.component.model.dto;

import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

public record ComponentDto(
    @NotBlank String code,
    @NotBlank String displayName,
    @NotNull @Valid Set<ComponentFieldDto> fields) {
  public ComponentDto {
    if (fields != null) {
      fields = new HashSet<>(fields);
    }
  }

  @Override
  public Set<ComponentFieldDto> fields() {
    return Set.copyOf(fields);
  }
}
