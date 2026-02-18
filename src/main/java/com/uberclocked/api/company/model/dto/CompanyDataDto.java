package com.uberclocked.api.company.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CompanyDataDto(
        UUID id,
        @NotBlank String name, @NotBlank String cuit, @NotBlank @Email String email, String phone) {}
