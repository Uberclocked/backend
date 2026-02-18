package com.uberclocked.api.payment.controller;

import com.uberclocked.api.payment.model.dto.InterestedInfoPaymentDto;
import com.uberclocked.api.payment.model.dto.InterestedInfoPreferenceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(MercadoPagoController.class);

  public MercadoPagoController(MercadoPagoService mpService) {
    this.mpService = mpService;
  }

  @PostMapping("/preference")
  public PreferenceDto createPreference(@AuthenticationPrincipal Jwt jwt) {
    log.info("Creating MercadoPago preference for user: {}", jwt.getSubject());

    PreferenceDto response = mpService.createPreference(jwt);

    log.info("Preference created successfully. Preference ID: {}", response.id());
    return response;
  }

  @PostMapping("/payment")
  public PaymentDto createPayment(
          @AuthenticationPrincipal Jwt jwt,
          @RequestBody MpBrickSubmitDto body) {

    log.info("Creating MercadoPago payment for user: {}", jwt.getSubject());
    log.debug("Payment request payload: {}", body);

    PaymentDto response = mpService.createPayment(jwt, body);

    log.info("Payment created successfully. Payment ID: {}", response.payment_id());
    return response;
  }

  @PostMapping("/payment/interested-info")
  public PaymentDto createInterestedInfoPayment(
          @AuthenticationPrincipal Jwt jwt,
          @RequestBody InterestedInfoPaymentDto body) {

    log.info("Creating InterestedInfo payment for user: {}", jwt.getSubject());

    PaymentDto response = mpService.createInterestedInfoPayment(jwt, body);

    log.info("InterestedInfo payment created. Payment ID: {}", response.payment_id());
    return response;
  }

  @PostMapping("/preference/interested-info")
  public PreferenceDto createInterestedInfoPreference(
          @AuthenticationPrincipal Jwt jwt,
          @RequestBody InterestedInfoPreferenceRequest body) {

    log.info("Creating InterestedInfo preference for user: {}", jwt.getSubject());

    PreferenceDto response = mpService.createInterestedInfoPreference(jwt, body);

    log.info("InterestedInfo preference created. Preference ID: {}", response.id());
    return response;
  }
}
