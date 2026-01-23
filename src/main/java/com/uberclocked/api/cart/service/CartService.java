package com.uberclocked.api.cart.service;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.cart.model.entity.CartStatus;
import com.uberclocked.api.cart.repository.CartItemRepository;
import com.uberclocked.api.cart.repository.CartRepository;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.service.ProductService;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CartService {

  private final CartRepository cartRepository;
  private final CartItemRepository itemRepository;
  private final ProductService productService;
  private final UsersService usersService;

  public CartService(
      CartRepository cartRepository,
      CartItemRepository itemRepository,
      ProductService productRepository,
      UsersService usersService) {
    this.cartRepository = cartRepository;
    this.itemRepository = itemRepository;
    this.productService = productRepository;
    this.usersService = usersService;
  }

  public Cart getOrCreateActiveCart(Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    return cartRepository
        .findByUserAndStatus(user, CartStatus.ACTIVE)
        .orElseGet(
            () -> {
              Cart cart = new Cart();
              cart.setUser(user);
              cart.setStatus(CartStatus.ACTIVE);
              cart.setCreatedAt(LocalDateTime.now());
              return cartRepository.save(cart);
            });
  }

  public Cart addItem(
      Jwt jwt, String productSku, Integer quantity, Map<String, String> components) {
    Cart cart = getOrCreateActiveCart(jwt);

    if (components != null && !components.isEmpty()) {
      double totalPrice = 0;
      for (Map.Entry<String, String> entry : components.entrySet()) {
        Product p = productService.getById(entry.getValue());
        if (p.getStock() < quantity)
          throw new IllegalArgumentException("Not enough stock for " + p.getName());
        totalPrice += p.getPrice();
      }
      CartItem item = new CartItem();
      item.setCart(cart);
      item.setName("Custom PC");
      item.setComponents(components);
      item.setQuantity(quantity);
      item.setTotalPrice(totalPrice);
      cart.getItems().add(item);
    } else {
      Product product = productService.getById(productSku);
      if (product.getStock() < quantity)
        throw new IllegalArgumentException("Not enough stock for " + product.getName());
      CartItem item = new CartItem();
      item.setCart(cart);
      item.setProduct(product);
      item.setQuantity(quantity);
      item.setTotalPrice(product.getPrice() * quantity);
      cart.getItems().add(item);
    }
    return cart;
  }

  public CartItem updateItem(Jwt jwt, UUID itemId, Integer quantity) {
    CartItem item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    item.setQuantity(quantity);
    return itemRepository.save(item);
  }

  public void removeItem(Jwt jwt, UUID itemId) {
    CartItem item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    itemRepository.delete(item);
  }

  public Cart checkout(Jwt jwt) {
    Cart cart = getOrCreateActiveCart(jwt);
    for (CartItem item : cart.getItems()) {
      int quantity = item.getQuantity();
      if (item.getProduct() != null) {

        productService.decreaseStock(item.getProduct().getSkuPrefix(), quantity);

      } else if (!item.getComponents().isEmpty()) {

        for (String sku : item.getComponents().values()) {

          productService.decreaseStock(sku, quantity);
        }
      }
    }

    cart.setStatus(CartStatus.COMPLETED);
    cart.setUpdatedAt(LocalDateTime.now());

    return cart;
  }

  public CartItem updateComponentInItem(
      Jwt jwt, UUID itemId, String componentType, String newProductSku) {
    CartItem item =
        itemRepository
            .findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    Product newProduct = productService.getById(newProductSku);
    if (newProduct.getStock() < item.getQuantity())
      throw new IllegalArgumentException("Not enough stock");
    item.getComponents().put(componentType, newProductSku);
    double totalPrice = 0;
    for (String sku : item.getComponents().values()) {
      totalPrice += productService.getById(sku).getPrice();
    }
    item.setTotalPrice(totalPrice);
    return itemRepository.save(item);
  }
}
