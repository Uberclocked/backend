package com.uberclocked.api.company.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CompanyDataDto(
    @NotBlank String name, @NotBlank String cuit, @NotBlank @Email String email, String phone) {}
