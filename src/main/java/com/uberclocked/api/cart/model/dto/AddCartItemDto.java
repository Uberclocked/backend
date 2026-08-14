package com.uberclocked.api.cart.model.dto;

import java.util.List;
import java.util.Map;

public record AddCartItemDto(String productSku, Integer quantity, Map<String, String> components) {}
