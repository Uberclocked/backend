package com.uberclocked.api.component.model.dto.field;

import com.uberclocked.api.component.model.entity.field.FieldType;

public record ComponentFieldDto(
    Long id,
    String name,
    FieldType type,
    boolean required,
    String defaultValue) {
}
