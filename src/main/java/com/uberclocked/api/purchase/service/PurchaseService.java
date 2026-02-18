package com.uberclocked.api.purchase.service;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.service.CartService;
import com.uberclocked.api.emailData.EmailService;
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
  private final EmailService emailService;

  public PurchaseService(
          PurchaseRepository purchaseRepository,
          CartService cartService,
          UsersService usersService,
          EmailService emailService
  ) {
    this.purchaseRepository = purchaseRepository;
    this.cartService = cartService;
    this.usersService = usersService;
    this.emailService = emailService;
  }

  public Purchase getPurchase(UUID id) {
    return purchaseRepository.getReferenceById(id);
  }

  public Purchase createPurchase(Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);

    Cart cart = cartService.checkout(jwt);

    if (cart.getItems() == null || cart.getItems().isEmpty()) {
      throw new IllegalStateException("Cart is empty");
    }

    double totalToPay = cartService.totalToPay(cart);

    Purchase purchase = new Purchase();
    purchase.setUser(user);
    purchase.setCart(cart);
    purchase.setTotalAmount(totalToPay);
    purchase.setStatus(PurchaseStatus.PAID);
    purchase.setCreatedAt(LocalDateTime.now());
    purchase.setUpdatedAt(LocalDateTime.now());

    return purchaseRepository.save(purchase);
  }

  public List<Purchase> getMyPurchases(Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    return purchaseRepository.findByUser(user);
  }

  public List<Purchase> getAllPurchases() {
    return purchaseRepository.findAll();
  }

  @Transactional
  public Purchase updatePurchase(UUID id, UpdatePurchaseDto dto, Jwt jwt) {
    Purchase purchase = purchaseRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

    PurchaseStatus oldStatus = purchase.getStatus();
    LocalDateTime oldPickupDate = purchase.getPickupDate();

    boolean statusChanged = false;
    boolean pickupChanged = false;

    if (dto.status() != null && dto.status() != oldStatus) {
      purchase.setStatus(dto.status());
      statusChanged = true;
    }

    if (dto.pickupDate() != null && !dto.pickupDate().equals(oldPickupDate)) {
      purchase.setPickupDate(dto.pickupDate());
      pickupChanged = true;
    }

    purchase.setUpdatedAt(LocalDateTime.now());

    Purchase saved = purchaseRepository.save(purchase);

    if (statusChanged || pickupChanged) {
      StringBuilder body = new StringBuilder();
      body.append("Your purchase has been updated.\n\n");

      if (statusChanged) {
        body.append("New status: ").append(saved.getStatus()).append("\n");
      }

      if (pickupChanged && saved.getPickupDate() != null) {
        body.append("Scheduled pickup date: ").append(saved.getPickupDate()).append("\n");
      }

      emailService.sendMail(
              saved.getUser().getEmail(),
              "Purchase update",
              body.toString()
      );
    }

    return saved;
  }

  public void deletePurchase(UUID id, Jwt jwt) {
    Purchase purchase = purchaseRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Purchase not found"));

    purchase.setStatus(PurchaseStatus.CANCELLED);
    purchaseRepository.save(purchase);
  }
}