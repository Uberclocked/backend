package com.uberclocked.api.purchase.mapper;

import com.uberclocked.api.cart.mapper.CartMapper;
import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.purchase.model.dto.PurchaseResponseDto;
import com.uberclocked.api.purchase.model.entity.Purchase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PurchaseMapper {

    private final CartMapper cartMapper;

    public PurchaseMapper(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    public PurchaseResponseDto toDto(Purchase purchase) {
        if (purchase == null) return null;

        var cart = purchase.getCart();

        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getStatus(),
                purchase.getTotalAmount(),
                purchase.getCreatedAt(),
                purchase.getUpdatedAt(),
                purchase.getPickupDate(),
                cart.getId(),
                cart.getItems() == null
                        ? List.<CartItemDto>of()
                        : cart.getItems().stream().map(cartMapper::toItemDto).toList()
        );
    }

    public List<PurchaseResponseDto> toDtoList(List<Purchase> purchases) {
        return purchases.stream().map(this::toDto).toList();
    }
}