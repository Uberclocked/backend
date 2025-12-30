package com.uberclocked.api.component.model.dto;

import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

public record ComponentDto(
    @NotBlank String skuPrefix,
    @NotBlank String displayName,
    @NotNull @Valid Map<String, ComponentFieldDto> fields) {
  public ComponentDto {
    if (fields != null) {
      fields = new HashMap<>(fields);
    }
  }

  @Override
  public Map<String, ComponentFieldDto> fields() {
    return Map.copyOf(fields);
  }
}
