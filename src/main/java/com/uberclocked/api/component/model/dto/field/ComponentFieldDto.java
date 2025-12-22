package com.uberclocked.api.component.model.dto.field;

import com.uberclocked.api.component.model.entity.field.FieldType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ComponentFieldDto(
    @NotBlank String name,
    @NotNull FieldType type,
    boolean required,
    String defaultValue) {
}
