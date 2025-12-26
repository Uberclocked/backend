package com.uberclocked.api.component.model.dto;

import java.util.Map;
import java.util.Optional;

import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;

public record UpdateComponentDto(
    Optional<String> displayName,
    Optional<Map<String, ComponentFieldDto>> fields) {
}
