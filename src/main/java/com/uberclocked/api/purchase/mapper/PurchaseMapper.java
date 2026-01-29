package com.uberclocked.api.purchase.mapper;

import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.purchase.model.dto.PurchaseResponseDto;
import com.uberclocked.api.purchase.model.entity.Purchase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseMapper {

    public PurchaseResponseDto toDto(Purchase purchase) {
        if (purchase == null) return null;

        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getStatus(),
                purchase.getTotalAmount(),
                purchase.getCreatedAt(),
                purchase.getUpdatedAt(),
                purchase.getPickupDate(),
                purchase.getCart().getId(),
                toCartItemDtoList(purchase.getCart().getItems())
        );
    }

    public List<PurchaseResponseDto> toDtoList(List<Purchase> purchases) {
        return purchases.stream().map(this::toDto).toList();
    }

    private List<CartItemDto> toCartItemDtoList(List<CartItem> items) {
        return items.stream().map(this::toCartItemDto).toList();
    }

    private CartItemDto toCartItemDto(CartItem item) {
        return new CartItemDto(
                item.getId(),
                item.getName(),
                item.getProduct() != null ? item.getProduct().getImage() : null,
                item.getQuantity(),
                item.getTotalPrice(),
                item.getProduct() != null ? item.getProduct().getSkuPrefix() : null,
                item.getProduct() != null ? item.getProduct().getName() : null,
                item.getComponents()
        );
    }
}