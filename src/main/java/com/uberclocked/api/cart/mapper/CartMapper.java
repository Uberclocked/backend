package com.uberclocked.api.cart.mapper;

import com.uberclocked.api.cart.model.dto.CartDto;
import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.service.ProductService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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

    public CartItemDto toItemDto(CartItem item) {
        String productSku = item.getProduct() != null ? item.getProduct().getSkuPrefix() : null;
        String productName = item.getProduct() != null ? item.getProduct().getName() : null;

        byte[] image = resolveCartItemImage(item);

        Integer stock = null;
        Integer availableStock = null;
        Map<String, Integer> componentsStock = null;

        if (item.getProduct() != null) {
            stock = item.getProduct().getStock();
            availableStock = stock;
        }
        else if (item.getComponents() != null && !item.getComponents().isEmpty()) {
            componentsStock = new HashMap<>();
            int min = Integer.MAX_VALUE;

            for (String sku : item.getComponents().values()) {
                Product p = productService.getById(sku);
                componentsStock.put(sku, p.getStock());
                min = Math.min(min, p.getStock());
            }

            availableStock = (min == Integer.MAX_VALUE) ? 0 : min;
            stock = availableStock;
        }

        return new CartItemDto(
                item.getId(),
                item.getName(),
                image,
                stock,
                availableStock,
                item.getQuantity(),
                item.getTotalPrice(),
                productSku,
                productName,
                item.getComponents(),
                componentsStock
        );
    }

    private byte[] resolveCartItemImage(CartItem item) {
        if (item.getProduct() != null) return item.getProduct().getImage();

        if (item.getComponents() == null || item.getComponents().isEmpty()) return null;

        String caseSku = item.getComponents().get("CASE");
        if (caseSku == null) caseSku = item.getComponents().get("CASE1"); // por si indexás
        if (caseSku == null || caseSku.isBlank()) return null;

        return productService.getById(caseSku).getImage();
    }
}