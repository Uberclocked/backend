package com.uberclocked.api.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentItemRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.cart.service.CartService;
import com.uberclocked.api.payment.model.dto.MpBrickSubmitDto;
import com.uberclocked.api.payment.model.dto.PaymentDto;
import com.uberclocked.api.payment.model.dto.PreferenceDto;
import com.uberclocked.api.payment.repository.MercadoPagoRepository;
import com.uberclocked.api.purchase.model.entity.Purchase;
import com.uberclocked.api.purchase.service.PurchaseService;

@Service
public class MercadoPagoService {
  private final MercadoPagoRepository mpRepository;
  private final CartService cartService;
  private final PurchaseService purchaseService;

  public MercadoPagoService(CartService cartService, PurchaseService purchaseService,
      MercadoPagoRepository mpRepository) {
    this.cartService = cartService;
    this.purchaseService = purchaseService;
    this.mpRepository = mpRepository;
  }

  @Transactional
  public PaymentDto createPayment(Jwt jwt, MpBrickSubmitDto body) {
    List<PaymentItemRequest> items = new ArrayList<>();
    Purchase myPurchase = purchaseService.createPurchase(jwt);
    for (CartItem item : myPurchase.getCart().items()) {
      items.add(
          PaymentItemRequest.builder()
              .id(item.id().toString())
              .title(item.name())
              .quantity(item.quantity())
              .currencyId("ARS")
              .unitPrice(
                  BigDecimal.valueOf(item.totalPrice())
                      .divide(BigDecimal.valueOf(item.quantity()), 2, RoundingMode.HALF_UP))
              .build());
    }
    Payment payment = mpRepository.createPayment(
        PaymentCreateRequest.builder()
            .token(body.token())
            .paymentMethodId(body.paymentMethodId())
            .issuerId(body.issuerId())
            .installments(body.installments())
            .transactionAmount(BigDecimal.valueOf(myPurchase.getTotalAmount()))
            .payer(
                PaymentPayerRequest.builder()
                    .email(body.payer().email())
                    .identification(
                        IdentificationRequest.builder()
                            .type(body.payer().identification().type())
                            .number(body.payer().identification().number())
                            .build())
                    .build())
            .externalReference(myPurchase.getId().toString())
            .build());
    return new PaymentDto(myPurchase.getId(), payment.getId());
  }

  public PreferenceDto createPreference(Jwt jwt) {
    List<PreferenceItemRequest> items = new ArrayList<>();
    Cart myCart = cartService.getOrCreateActiveCart(jwt);
    if (myCart.items().isEmpty()) {
      throw new RuntimeException("Can't buy cart with no products");
    }
    for (CartItem item : myCart.items()) {
      items.add(
          PreferenceItemRequest.builder()
              .id(item.id().toString())
              .title(item.name())
              .quantity(item.quantity())
              .currencyId("ARS")
              .unitPrice(
                  BigDecimal.valueOf(item.totalPrice())
                      .divide(BigDecimal.valueOf(item.quantity()), 2, RoundingMode.HALF_UP))
              .build());
    }
    PreferenceRequest request = PreferenceRequest.builder()
        .items(items)
        .externalReference("a")
        .backUrls(
            PreferenceBackUrlsRequest.builder()
                .success("http://localhost:3000/checkout/success")
                .failure("http://localhost:3000/checkout/failure")
                .pending("http://localhost:3000/checkout/pending")
                .build())
        .notificationUrl("http://localhost:3000/checkout/notify")
        .build();
    return new PreferenceDto(mpRepository.createPreference(request).getId());
  }
}
