package com.uberclocked.api.component.model.dto;

import java.util.Set;
import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ComponentDto(
    @NotBlank String code,
    @NotBlank String displayName,
    @NotEmpty @Valid Set<ComponentFieldDto> fields) {
}
