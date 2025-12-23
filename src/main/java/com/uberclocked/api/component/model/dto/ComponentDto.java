package com.uberclocked.api.component.model.dto;

import java.util.Set;
import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record ComponentDto(
    @NotBlank String code,
    @NotBlank String displayName,
    @Valid Set<ComponentFieldDto> fields) {
}
