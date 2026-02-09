package com.uberclocked.api.payment.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uberclocked.api.payment.model.dto.MpBrickSubmitDto;
import com.uberclocked.api.payment.model.dto.PaymentDto;
import com.uberclocked.api.payment.model.dto.PreferenceDto;
import com.uberclocked.api.payment.service.MercadoPagoService;

@RestController
@RequestMapping("/mp")
public class MercadoPagoController {
  private final MercadoPagoService mpService;

  public MercadoPagoController(MercadoPagoService mpService) {
    this.mpService = mpService;
  }

  @PostMapping("/preference")
  public PreferenceDto createPreference(
      @AuthenticationPrincipal Jwt jwt) {
    return mpService.createPreference(jwt);
  }

  @PostMapping("/payment")
  public PaymentDto createPayment(
      @AuthenticationPrincipal Jwt jwt,
      @RequestBody MpBrickSubmitDto body) {
    return mpService.createPayment(jwt, body);
  }
}
