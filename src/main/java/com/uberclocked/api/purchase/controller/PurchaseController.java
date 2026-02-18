package com.uberclocked.api.purchase.controller;

import com.uberclocked.api.purchase.mapper.PurchaseMapper;
import com.uberclocked.api.purchase.model.dto.PurchaseResponseDto;
import com.uberclocked.api.purchase.model.dto.UpdatePurchaseDto;
import com.uberclocked.api.purchase.service.PurchaseService;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
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
  private final PurchaseMapper purchaseMapper;

  public PurchaseController(PurchaseService purchaseService, PurchaseMapper purchaseMapper) {
    this.purchaseService = purchaseService;
    this.purchaseMapper = purchaseMapper;
  }

  @PostMapping("/me")
  public PurchaseResponseDto create(@AuthenticationPrincipal Jwt jwt) {
    return purchaseMapper.toDto(purchaseService.createPurchase(jwt));
  }

  @GetMapping("/me")
  public List<PurchaseResponseDto> myPurchases(@AuthenticationPrincipal Jwt jwt) {
    return purchaseMapper.toDtoList(purchaseService.getMyPurchases(jwt));
  }

  @GetMapping
  @PreAuthorize("hasRole('Admin')")
  public List<PurchaseResponseDto> getAll(@AuthenticationPrincipal Jwt jwt) {
    return purchaseMapper.toDtoList(purchaseService.getAllPurchases());
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('Admin')")
  public PurchaseResponseDto update(
      @PathVariable UUID id, @RequestBody UpdatePurchaseDto dto, @AuthenticationPrincipal Jwt jwt) {
    return purchaseMapper.toDto(purchaseService.updatePurchase(id, dto, jwt));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('Admin')")
  public void delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
    purchaseService.deletePurchase(id, jwt);
  }
}
