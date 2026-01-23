package com.uberclocked.api.cart.controller;

import com.uberclocked.api.cart.model.dto.AddCartItemDto;
import com.uberclocked.api.cart.model.entity.Cart;
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
  public Cart getMyCart(@AuthenticationPrincipal Jwt jwt) {
    return cartService.getOrCreateActiveCart(jwt);
  }

  @PostMapping("/me/items")
  public Cart addItem(@AuthenticationPrincipal Jwt jwt, @RequestBody AddCartItemDto dto) {

    return cartService.addItem(jwt, dto.productSku(), dto.quantity(), dto.components());
  }

  @PatchMapping("/me/items/{itemId}")
  public CartItem updateItem(
      @PathVariable UUID itemId, @RequestParam Integer quantity, @AuthenticationPrincipal Jwt jwt) {

    return cartService.updateItem(jwt, itemId, quantity);
  }

  @PatchMapping("/me/items/{itemId}/components")
  public CartItem updateComponent(
      @PathVariable UUID itemId,
      @RequestParam String componentType,
      @RequestParam String newProductSku,
      @AuthenticationPrincipal Jwt jwt) {

    return cartService.updateComponentInItem(jwt, itemId, componentType, newProductSku);
  }

  @DeleteMapping("/me/items/{itemId}")
  public void removeItem(@PathVariable UUID itemId, @AuthenticationPrincipal Jwt jwt) {

    cartService.removeItem(jwt, itemId);
  }

  @PostMapping("/me/checkout")
  public Cart checkout(@AuthenticationPrincipal Jwt jwt) {
    return cartService.checkout(jwt);
  }
}
