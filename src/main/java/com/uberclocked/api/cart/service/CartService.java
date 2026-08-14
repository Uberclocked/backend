package com.uberclocked.api.cart.service;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.cart.model.entity.CartStatus;
import com.uberclocked.api.cart.repository.CartItemRepository;
import com.uberclocked.api.cart.repository.CartRepository;
import com.uberclocked.api.product.model.entity.Product;
import com.uberclocked.api.product.service.ProductService;
import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.promotion.service.PromotionService;
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

  // ✅ Cargo fijo SIEMPRE al total final (no al item)
  public static final double CHECKOUT_FEE = 50.0;

  private final CartRepository cartRepository;
  private final CartItemRepository itemRepository;
  private final ProductService productService;
  private final UsersService usersService;
  private final PromotionService promotionService;

  public CartService(
          CartRepository cartRepository,
          CartItemRepository itemRepository,
          ProductService productRepository,
          UsersService usersService,
          PromotionService promotionService
  ) {
    this.cartRepository = cartRepository;
    this.itemRepository = itemRepository;
    this.productService = productRepository;
    this.usersService = usersService;
    this.promotionService = promotionService;
  }

  public Cart getCart(UUID id) {
    return cartRepository.getReferenceById(id);
  }

  public Cart getOrCreateActiveCart(Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    return cartRepository
            .findByUserAndStatus(user, CartStatus.ACTIVE)
            .orElseGet(() -> {
              Cart cart = new Cart();
              cart.setUser(user);
              cart.setStatus(CartStatus.ACTIVE);
              cart.setCreatedAt(LocalDateTime.now());
              cart.setUpdatedAt(LocalDateTime.now());
              return cartRepository.save(cart);
            });
  }

  // ✅ Helpers (por si querés exponerlo desde controller/mapper)
  public double subtotal(Cart cart) {
    if (cart == null || cart.getItems() == null) return 0.0;
    return cart.getItems().stream().mapToDouble(CartItem::getTotalPrice).sum();
  }

  public double totalToPay(Cart cart) {
    double sub = subtotal(cart);
    double discount = cart == null || cart.getDiscountAmount() == null ? 0.0 : cart.getDiscountAmount();
    return Math.max(0.0, sub + CHECKOUT_FEE - discount);
  }

  public Cart addItem(Jwt jwt, String productSku, Integer quantity, Map<String, String> components) {
    Cart cart = getOrCreateActiveCart(jwt);

    if (quantity == null || quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

    if (components != null && !components.isEmpty()) {
      String caseSku = components.get("CASE");
      if (caseSku == null || caseSku.isBlank()) throw new IllegalArgumentException("Custom PC requires CASE");

      double totalPrice = 0;
      for (Map.Entry<String, String> entry : components.entrySet()) {
        String sku = entry.getValue();
        Product p = productService.getById(sku);
        if (p.getStock() < quantity) throw new IllegalArgumentException("Not enough stock for " + p.getName());
        totalPrice += p.getPrice();
      }

      CartItem item = new CartItem();
      item.setCart(cart);
      item.setName("Custom PC");
      item.setComponents(components);
      item.setQuantity(quantity);
      item.setCreatedAt(LocalDateTime.now());
      // ✅ NO sumamos 50 acá. El 50 va al total final del carrito.
      item.setTotalPrice(totalPrice * quantity);
      cart.getItems().add(item);

      cart.setUpdatedAt(LocalDateTime.now());
      recalculatePromotion(cart);
      return cartRepository.save(cart);
    }

    Product product = productService.getById(productSku);

    CartItem existing = cart.getItems().stream()
            .filter(i -> i.getProduct() != null)
            .filter(i -> productSku.equals(i.getProduct().getSkuPrefix()))
            .findFirst()
            .orElse(null);

    if (existing != null) {
      int newQty = existing.getQuantity() + quantity;
      if (product.getStock() < newQty) throw new IllegalArgumentException("Not enough stock for " + product.getName());
      existing.setQuantity(newQty);
      existing.setTotalPrice(product.getPrice() * newQty);
    } else {
      if (product.getStock() < quantity) throw new IllegalArgumentException("Not enough stock for " + product.getName());

      CartItem item = new CartItem();
      item.setName(product.getName());
      item.setCart(cart);
      item.setProduct(product);
      item.setQuantity(quantity);
      item.setCreatedAt(LocalDateTime.now());
      item.setTotalPrice(product.getPrice() * quantity);
      cart.getItems().add(item);
    }

    cart.setUpdatedAt(LocalDateTime.now());
    recalculatePromotion(cart);
    return cartRepository.save(cart);
  }

  public CartItem setItemQuantity(Jwt jwt, UUID itemId, Integer quantity) {
    if (quantity == null) throw new IllegalArgumentException("quantity is required");

    User user = usersService.getUserOrCreate(jwt);
    Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow();

    CartItem item = itemRepository
            .findByIdAndCartId(itemId, cart.getId())
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    if (quantity <= 0) {
      itemRepository.delete(item);
      cart.setUpdatedAt(LocalDateTime.now());
      recalculatePromotion(cart);
      cartRepository.save(cart);
      return item;
    }

    if (item.getProduct() != null) {
      Product p = productService.getById(item.getProduct().getSkuPrefix());
      if (p.getStock() < quantity) throw new IllegalArgumentException("Not enough stock for " + p.getName());
      item.setQuantity(quantity);
      item.setTotalPrice(item.getProduct().getPrice() * quantity);
    } else {
      if (item.getComponents() == null || item.getComponents().isEmpty()) {
        throw new IllegalArgumentException("Custom PC has no components");
      }
      for (String sku : item.getComponents().values()) {
        Product p = productService.getById(sku);
        if (p.getStock() < quantity) throw new IllegalArgumentException("Not enough stock for " + p.getName());
      }

      item.setQuantity(quantity);
      double total = 0;
      for (String sku : item.getComponents().values()) total += productService.getById(sku).getPrice();
      item.setTotalPrice(total * quantity);
    }

    CartItem savedItem = itemRepository.save(item);

    cart.setUpdatedAt(LocalDateTime.now());
    recalculatePromotion(cart);
    cartRepository.save(cart);

    return savedItem;
  }

  public void removeItem(Jwt jwt, UUID itemId) {
    User user = usersService.getUserOrCreate(jwt);
    Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow();

    CartItem item = itemRepository
            .findByIdAndCartId(itemId, cart.getId())
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    itemRepository.delete(item);

    cart.setUpdatedAt(LocalDateTime.now());
    recalculatePromotion(cart);
    cartRepository.save(cart);
  }

  public Cart checkout(Jwt jwt) {
    Cart cart = getOrCreateActiveCart(jwt);

    if (cart.getAppliedPromotion() != null) {
      Promotion promo = cart.getAppliedPromotion();
      promotionService.assertCanApply(cart.getUser().getId(), promo, cart);
      recalculatePromotion(cart);
    }

    for (CartItem item : cart.getItems()) {
      int quantity = item.getQuantity();
      if (item.getProduct() != null) {
        productService.decreaseStock(item.getProduct().getSkuPrefix(), quantity);
      } else if (item.getComponents() != null && !item.getComponents().isEmpty()) {
        for (String sku : item.getComponents().values()) {
          productService.decreaseStock(sku, quantity);
        }
      }
    }

    if (cart.getAppliedPromotion() != null) {
      promotionService.consumePromotion(cart.getAppliedPromotion().getId());
    }

    cart.setStatus(CartStatus.COMPLETED);
    cart.setUpdatedAt(LocalDateTime.now());

    return cartRepository.save(cart);
  }

  public CartItem updateComponentInItem(Jwt jwt, UUID itemId, String componentType, String newProductSku) {
    User user = usersService.getUserOrCreate(jwt);
    Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow();

    CartItem item = itemRepository
            .findByIdAndCartId(itemId, cart.getId())
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    Product newProduct = productService.getById(newProductSku);
    if (newProduct.getStock() < item.getQuantity()) throw new IllegalArgumentException("Not enough stock");

    item.getComponents().put(componentType, newProductSku);

    double totalPrice = 0;
    for (String sku : item.getComponents().values()) totalPrice += productService.getById(sku).getPrice();
    item.setTotalPrice(totalPrice * item.getQuantity());

    CartItem saved = itemRepository.save(item);

    cart.setUpdatedAt(LocalDateTime.now());
    recalculatePromotion(cart);
    cartRepository.save(cart);

    return saved;
  }

  public CartItem replaceComponents(Jwt jwt, UUID itemId, Map<String, String> newComponents) {
    if (newComponents == null || newComponents.isEmpty()) throw new IllegalArgumentException("components are required");

    String caseSku = newComponents.get("CASE");
    if (caseSku == null || caseSku.isBlank()) throw new IllegalArgumentException("Custom PC requires CASE");

    User user = usersService.getUserOrCreate(jwt);
    Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow();

    CartItem item = itemRepository
            .findByIdAndCartId(itemId, cart.getId())
            .orElseThrow(() -> new IllegalArgumentException("Item not found"));

    int qty = item.getQuantity();
    double total = 0;

    for (String sku : newComponents.values()) {
      Product p = productService.getById(sku);
      if (p.getStock() < qty) throw new IllegalArgumentException("Not enough stock for " + p.getName());
      total += p.getPrice();
    }

    item.setComponents(newComponents);
    item.setName("Custom PC");
    item.setTotalPrice(total * qty);

    CartItem saved = itemRepository.save(item);

    cart.setUpdatedAt(LocalDateTime.now());
    recalculatePromotion(cart);
    cartRepository.save(cart);

    return saved;
  }

  private void recalculatePromotion(Cart cart) {
    if (cart == null || cart.getAppliedPromotion() == null) {
      cart.setDiscountAmount(null);
      return;
    }

    Promotion promo = cart.getAppliedPromotion();

    boolean ok = promotionService.canApplyPromotion(cart.getUser().getId(), promo, cart);
    if (!ok) {
      cart.setAppliedPromotion(null);
      cart.setDiscountAmount(null);
      return;
    }

    double eligibleSubtotal = cart.getItems().stream()
            .filter(i -> promotionService.appliesToItem(promo, i))
            .mapToDouble(CartItem::getTotalPrice)
            .sum();

    if (eligibleSubtotal <= 0) {
      cart.setAppliedPromotion(null);
      cart.setDiscountAmount(null);
      return;
    }

    double discount = eligibleSubtotal * (promo.getDiscount() / 100.0);
    cart.setDiscountAmount(discount);
  }
}