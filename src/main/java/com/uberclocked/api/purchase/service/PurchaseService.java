package com.uberclocked.api.purchase.service;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.cart.model.entity.CartStatus;
import com.uberclocked.api.cart.service.CartService;
import com.uberclocked.api.purchase.model.dto.UpdatePurchaseDto;
import com.uberclocked.api.purchase.model.entity.Purchase;
import com.uberclocked.api.purchase.model.entity.PurchaseStatus;
import com.uberclocked.api.purchase.repository.PurchaseRepository;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PurchaseService {

  private final PurchaseRepository purchaseRepository;
  private final CartService cartService;
  private final UsersService usersService;

  public PurchaseService(
      PurchaseRepository purchaseRepository, CartService cartService, UsersService usersService) {
    this.purchaseRepository = purchaseRepository;
    this.cartService = cartService;
    this.usersService = usersService;
  }

  public Purchase createPurchase(Jwt jwt) {

    User user = usersService.getUserOrCreate(jwt);
    Cart cart = cartService.getOrCreateActiveCart(jwt);

    if (cart.getItems().isEmpty()) {
      throw new IllegalStateException("Cart is empty");
    }

    double total = 0;
    for (CartItem item : cart.getItems()) {
      total += item.getTotalPrice();
    }

    Purchase purchase = new Purchase();
    purchase.setUser(user);
    purchase.setItems(cart.getItems());
    purchase.setTotalAmount(total);
    purchase.setStatus(PurchaseStatus.CREATED);
    purchase.setCreatedAt(LocalDateTime.now());

    cart.setStatus(CartStatus.COMPLETED);

    return purchaseRepository.save(purchase);
  }

  public List<Purchase> getMyPurchases(Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    return purchaseRepository.findByUser(user);
  }

  public List<Purchase> getAllPurchases() {
    return purchaseRepository.findAll();
  }

  public Purchase updatePurchase(UUID id, UpdatePurchaseDto dto, Jwt jwt) {

    checkAdmin(jwt);

    Purchase purchase =
        purchaseRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

    if (dto.status() != null) {
      purchase.setStatus(dto.status());
    }

    if (dto.pickupDate() != null) {
      purchase.setPickupDate(dto.pickupDate());
    }

    purchase.setUpdatedAt(LocalDateTime.now());
    return purchaseRepository.save(purchase);
  }

  public void deletePurchase(UUID id, Jwt jwt) {

    checkAdmin(jwt);

    Purchase purchase =
        purchaseRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

    purchase.setStatus(PurchaseStatus.CANCELLED);
    purchaseRepository.save(purchase);
  }

  private void checkAdmin(Jwt jwt) {
    List<String> roles = jwt.getClaimAsStringList("https://uberclocked.com/roles");

    boolean isAdmin = roles != null && roles.contains("ADMIN");

    if (!isAdmin) {
      throw new SecurityException("Only admins can perform this action");
    }
  }
}
