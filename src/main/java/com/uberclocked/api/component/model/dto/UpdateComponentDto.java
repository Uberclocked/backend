package com.uberclocked.api.component.model.dto;

import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import java.util.HashMap;
import java.util.Map;

public record UpdateComponentDto(String displayName, Map<String, ComponentFieldDto> fields) {
  public UpdateComponentDto {
    fields = fields == null ? null : new HashMap<>(fields);
  }

  @Override
  public Map<String, ComponentFieldDto> fields() {
    return fields == null ? null : new HashMap<>(fields);
  }
}
