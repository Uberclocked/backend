package com.uberclocked.api.component.model.dto;

import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ComponentDto(
    @NotBlank String code,
    @NotBlank String displayName,
    @NotNull @Valid Set<ComponentFieldDto> fields) {}
