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

  public CartController(CartService cartService, ProductService productService, CartMapper mapper) {
    this.cartService = cartService;
    this.productService = productService;
    this.mapper = mapper;
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
  public CartItemDto updateItem(
      @PathVariable UUID itemId,
      @RequestParam Integer quantity,
      @AuthenticationPrincipal Jwt jwt) {

    CartItem item = cartService.setItemQuantity(jwt, itemId, quantity);

    byte[] image = resolveCartItemImage(item);
    String sku = item.getProduct() != null ? item.getProduct().getSkuPrefix() : null;
    String name = item.getProduct() != null ? item.getProduct().getName() : null;
    Integer stock = item.getProduct() != null ? item.getProduct().getStock() : null;

    return new CartItemDto(
            itemId,
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

  @PatchMapping("/me/items/{itemId}/components")
  public CartDto updateComponent(
          @PathVariable UUID itemId,
          @RequestParam String componentType,
          @RequestParam String newProductSku,
          @AuthenticationPrincipal Jwt jwt) {

    cartService.updateComponentInItem(jwt, itemId, componentType, newProductSku);
    Cart cart = cartService.getOrCreateActiveCart(jwt);
    return toDtoWithResolvedImages(cart);
  }

  @DeleteMapping("/me/items/{itemId}")
  public CartDto removeItem(
          @PathVariable UUID itemId,
          @AuthenticationPrincipal Jwt jwt) {

    cartService.removeItem(jwt, itemId);
    Cart cart = cartService.getOrCreateActiveCart(jwt);
    return toDtoWithResolvedImages(cart);
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

  private CartDto toDtoWithResolvedImages(Cart cart) {
    var items = cart.getItems() == null
            ? java.util.List.<CartItemDto>of()
            : cart.getItems().stream().map(this::toItemDtoWithImage).toList();
    return new CartDto(
            cart.getId(),
            cart.getCreatedAt(),
            cart.getUpdatedAt(),
            cart.getStatus() != null ? cart.getStatus().name() : null,
            items
    );
  }

  private CartItemDto toItemDtoWithImage(CartItem item) {
    byte[] image = resolveCartItemImage(item);

    String sku = item.getProduct() != null ? item.getProduct().getSkuPrefix() : null;
    String name = item.getProduct() != null ? item.getProduct().getName() : null;
    Integer stock = item.getProduct() != null ? item.getProduct().getStock() : null;

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

  @PatchMapping("/me/items/{itemId}/components/bulk")
  public CartItemDto replaceComponents(
          @PathVariable UUID itemId,
          @RequestBody UpdateCartItemComponentsDto dto,
          @AuthenticationPrincipal Jwt jwt
  ) {
    CartItem item = cartService.replaceComponents(jwt, itemId, dto.components());

    byte[] image = resolveCartItemImage(item);
    String sku = item.getProduct() != null ? item.getProduct().getSkuPrefix() : null;
    String name = item.getProduct() != null ? item.getProduct().getName() : null;
    Integer stock = item.getProduct() != null ? item.getProduct().getStock() : null;

    return new CartItemDto(
            itemId,
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
