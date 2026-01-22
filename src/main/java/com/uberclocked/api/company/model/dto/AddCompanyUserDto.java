package com.uberclocked.api.company.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddCompanyUserDto(@NotBlank @Email String email) {}
