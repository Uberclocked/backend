package com.uberclocked.api.component.model.dto;

import java.util.Set;

import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;

public record ComponentDto(
    String code,
    String displayName,
    Set<ComponentFieldDto> fields) {
}
