package com.uberclocked.api.cart.controller;

import com.uberclocked.api.cart.mapper.CartMapper;
import com.uberclocked.api.cart.model.dto.AddCartItemDto;
import com.uberclocked.api.cart.model.dto.CartDto;
import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.cart.service.CartService;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
public class CartController {

  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping("/me")
  public CartDto getMyCart(@AuthenticationPrincipal Jwt jwt) {
    return CartMapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }

  @PostMapping("/me/items")
  public CartDto addItem(
      @AuthenticationPrincipal Jwt jwt,
      @RequestBody AddCartItemDto dto) {

    return CartMapper.toDto(
        cartService.addItem(jwt, dto.productSku(), dto.quantity(), dto.components()));
  }

  @PatchMapping("/me/items/{itemId}")
  public CartItemDto updateItem(
      @PathVariable UUID itemId,
      @RequestParam Integer quantity,
      @AuthenticationPrincipal Jwt jwt) {

    CartItem item = cartService.setItemQuantity(jwt, itemId, quantity);
    return new CartItemDto(itemId, item.getName(), item.getProduct().getImage(), item.getQuantity(),
        item.getTotalPrice(), item.getProduct().getSkuPrefix(), item.getProduct().getName(), item.getComponents());

  }

  @PatchMapping("/me/items/{itemId}/components")
  public CartDto updateComponent(
      @PathVariable UUID itemId,
      @RequestParam String componentType,
      @RequestParam String newProductSku,
      @AuthenticationPrincipal Jwt jwt) {

    cartService.updateComponentInItem(jwt, itemId, componentType, newProductSku);
    return CartMapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }

  @DeleteMapping("/me/items/{itemId}")
  public CartDto removeItem(
      @PathVariable UUID itemId,
      @AuthenticationPrincipal Jwt jwt) {

    cartService.removeItem(jwt, itemId);
    return CartMapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }

  @PostMapping("/me/checkout")
  public CartDto checkout(@AuthenticationPrincipal Jwt jwt) {
    return CartMapper.toDto(cartService.checkout(jwt));
  }
}
