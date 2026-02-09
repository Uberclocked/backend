package com.uberclocked.api.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class MercadoPagoPaymentConfig {

  @Value("${mp.api-token}")
  private String accessToken;

  @PostConstruct
  public void init() {
    MercadoPagoConfig.setAccessToken(accessToken);
  }

  @Bean
  public PreferenceClient preferenceClient() {
    return new PreferenceClient();
  }

  @Bean
  public PaymentClient paymentClient() {
    return new PaymentClient();
  }
}
