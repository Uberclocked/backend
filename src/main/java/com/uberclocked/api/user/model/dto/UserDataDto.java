package com.uberclocked.api.user.model.dto;

import java.util.UUID;

public record UserDataDto(UUID id, String userName, String email, String country, String cellPhone) {}