package com.uberclocked.api.purchase.controller;

import com.uberclocked.api.purchase.model.dto.UpdatePurchaseDto;
import com.uberclocked.api.purchase.model.entity.Purchase;
import com.uberclocked.api.purchase.service.PurchaseService;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

  private final PurchaseService purchaseService;

  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  @PostMapping("/me")
  public Purchase create(@AuthenticationPrincipal Jwt jwt) {
    return purchaseService.createPurchase(jwt);
  }

  @GetMapping("/me")
  public List<Purchase> myPurchases(@AuthenticationPrincipal Jwt jwt) {
    return purchaseService.getMyPurchases(jwt);
  }

  @GetMapping
  public List<Purchase> getAll(@AuthenticationPrincipal Jwt jwt) {
    return purchaseService.getAllPurchases();
  }

  @PatchMapping("/{id}")
  public Purchase update(
      @PathVariable UUID id, @RequestBody UpdatePurchaseDto dto, @AuthenticationPrincipal Jwt jwt) {

    return purchaseService.updatePurchase(id, dto, jwt);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {

    purchaseService.deletePurchase(id, jwt);
  }
}
