package com.uberclocked.api.product.model.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public record ProductDataDto(
        String sku,
        String name,
        String componentSkuPrefix,
        Double price,
        int stock,
        Map<String, String> attributes
) {}