package com.uberclocked.api.cart.controller;

import com.uberclocked.api.cart.mapper.CartMapper;
import com.uberclocked.api.cart.model.dto.AddCartItemDto;
import com.uberclocked.api.cart.model.dto.CartDto;
import com.uberclocked.api.cart.model.dto.CartItemDto;
import com.uberclocked.api.cart.model.dto.UpdateCartItemComponentsDto;
import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.cart.service.CartService;
import java.util.UUID;

import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.service.ProductService;
import com.uberclocked.api.promotion.model.dto.ApplyCouponRequest;
import com.uberclocked.api.promotion.service.CartPromotionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
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
  private final ProductService productService;
  private final CartMapper mapper;
  private final CartPromotionService cartPromotionService;

  public CartController(CartService cartService, ProductService productService, CartMapper mapper, CartPromotionService cartPromotionService) {
    this.cartService = cartService;
    this.productService = productService;
    this.mapper = mapper;
    this.cartPromotionService = cartPromotionService;
  }

  @GetMapping("/me")
  public CartDto getMyCart(@AuthenticationPrincipal Jwt jwt) {
    return mapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }

  @PostMapping("/me/items")
  public CartDto addItem(
      @AuthenticationPrincipal Jwt jwt,
      @RequestBody AddCartItemDto dto) {

    return mapper.toDto(
        cartService.addItem(jwt, dto.productSku(), dto.quantity(), dto.components()));
  }

  @PatchMapping("/me/items/{itemId}")
  public CartDto updateItem(
          @PathVariable UUID itemId,
          @RequestParam Integer quantity,
          @AuthenticationPrincipal Jwt jwt) {

    cartService.setItemQuantity(jwt, itemId, quantity);
    return mapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }

  @PatchMapping("/me/items/{itemId}/components")
  public CartDto updateComponent(
          @PathVariable UUID itemId,
          @RequestParam String componentType,
          @RequestParam String newProductSku,
          @AuthenticationPrincipal Jwt jwt) {

    cartService.updateComponentInItem(jwt, itemId, componentType, newProductSku);
    return mapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }

  @DeleteMapping("/me/items/{itemId}")
  public CartDto removeItem(@PathVariable UUID itemId, @AuthenticationPrincipal Jwt jwt) {
    cartService.removeItem(jwt, itemId);
    return mapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }


  @PostMapping("/me/checkout")
  public CartDto checkout(@AuthenticationPrincipal Jwt jwt) {
    return mapper.toDto(cartService.checkout(jwt));
  }

  private byte[] resolveCartItemImage(CartItem item) {
    if (item.getProduct() != null) return item.getProduct().getImage();

    if (item.getComponents() == null || item.getComponents().isEmpty()) return null;

    String caseSku = item.getComponents().get("CASE");
    if (caseSku == null) caseSku = item.getComponents().get("CASE1"); // por si indexás

    if (caseSku == null || caseSku.isBlank()) return null;

    Product caseProduct = productService.getById(caseSku);
    return caseProduct.getImage();
  }

  @PatchMapping("/me/items/{itemId}/components/bulk")
  public CartDto replaceComponents(
          @PathVariable UUID itemId,
          @RequestBody UpdateCartItemComponentsDto dto,
          @AuthenticationPrincipal Jwt jwt) {

    cartService.replaceComponents(jwt, itemId, dto.components());
    return mapper.toDto(cartService.getOrCreateActiveCart(jwt));
  }

  @PostMapping("/coupon/apply")
  public CartDto apply(@AuthenticationPrincipal Jwt jwt, @RequestBody ApplyCouponRequest req) {
    try {
      return mapper.toDto(cartPromotionService.applyCoupon(jwt, req.code()));
    }catch (Exception e) {
      throw new EntityNotFoundException("Coupon not found: " + req.code());
    }
  }

  @PostMapping("/coupon/remove")
  public CartDto remove(@AuthenticationPrincipal Jwt jwt) {
    try {
      return mapper.toDto(cartPromotionService.removeCoupon(jwt));
    }catch (Exception e) {
    throw new EntityNotFoundException("Coupon not found");}
  }
}
