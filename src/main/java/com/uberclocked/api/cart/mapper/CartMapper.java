package com.uberclocked.api.cart.mapper;

import com.uberclocked.api.cart.model.dto.CartDto;
import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.product.service.ProductService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CartMapper {

    private final ProductService productService;

    public CartMapper(ProductService productService) {
        this.productService = productService;
    }

    public CartDto toDto(Cart cart) {
        var items = cart.getItems() == null
                ? List.<CartItemDto>of()
                : cart.getItems().stream().map(this::toItemDto).toList();

        return new CartDto(
                cart.getId(),
                cart.getCreatedAt(),
                cart.getUpdatedAt(),
                cart.getStatus() != null ? cart.getStatus().name() : null,
                items
        );
    }

    private CartItemDto toItemDto(CartItem item) {
        String sku = item.getProduct() != null ? item.getProduct().getSkuPrefix() : null;
        String name = item.getProduct() != null ? item.getProduct().getName() : null;
        Integer stock = item.getProduct() != null ? item.getProduct().getStock() : null;

        byte[] image = null;

        if (item.getProduct() != null) {
            image = item.getProduct().getImage();
        } else if (item.getComponents() != null) {
            String caseSku = item.getComponents().get("CASE");
            if (caseSku != null && !caseSku.isBlank()) {
                image = productService.getById(caseSku).getImage();
            }
        }

        return new CartItemDto(
                item.getId(),
                item.getName(),
                image,
                stock,
                item.getQuantity(),
                item.getTotalPrice(),
                sku,
                name,
                item.getComponents()
        );
    }
}