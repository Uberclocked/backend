package com.uberclocked.api.cart.model.dto;

import java.util.Map;

public record UpdateCartItemComponentsDto(
        Map<String, String> components
) {}