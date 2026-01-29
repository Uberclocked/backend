package com.uberclocked.api.cart.mapper;

import com.uberclocked.api.cart.model.dto.CartDto;
import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import java.util.List;

public class CartMapper {

    public static CartDto toDto(Cart cart) {
        List<CartItemDto> items = cart.getItems() == null
                ? List.of()
                : cart.getItems().stream().map(CartMapper::toItemDto).toList();

        return new CartDto(
                cart.getId(),
                cart.getCreatedAt(),
                cart.getUpdatedAt(),
                cart.getStatus() != null ? cart.getStatus().name() : null,
                items
        );
    }

    private static CartItemDto toItemDto(CartItem item) {
        String sku = item.getProduct() != null ? item.getProduct().getSkuPrefix() : null;
        String name = item.getProduct() != null ? item.getProduct().getName() : null;
        byte[] image = item.getProduct() != null ? item.getProduct().getImage() : null;

        return new CartItemDto(
                item.getId(),
                item.getName(),
                image,
                item.getQuantity(),
                item.getTotalPrice(),
                sku,
                name,
                item.getComponents()
        );
    }
}