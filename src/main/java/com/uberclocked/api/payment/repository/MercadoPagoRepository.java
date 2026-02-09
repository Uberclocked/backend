package com.uberclocked.api.payment.repository;

import org.springframework.stereotype.Repository;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

@Repository
public class MercadoPagoRepository {
  private final PreferenceClient preferenceClient;
  private final PaymentClient paymentClient;

  public MercadoPagoRepository(PreferenceClient preferenceClient, PaymentClient paymentClient) {
    this.preferenceClient = preferenceClient;
    this.paymentClient = paymentClient;
  }

  public Payment createPayment(PaymentCreateRequest request) {
    try {
      return paymentClient.create(request);

    } catch (MPApiException e) {
      System.err.println("=== MERCADO PAGO API ERROR ===");
      System.err.println("Status: " + e.getStatusCode());
      System.err.println("Response: " + e.getApiResponse().getContent());
      System.err.println("================================");

      throw new RuntimeException("Mercado Pago API error", e);

    } catch (MPException e) {
      System.err.println("=== MERCADO PAGO SDK ERROR ===");
      e.printStackTrace();

      throw new RuntimeException("Mercado Pago SDK error", e);
    }
  }

  public Preference createPreference(PreferenceRequest request) {
    try {
      return preferenceClient.create(request);
    } catch (MPException | MPApiException exception) {
      throw new RuntimeException("Error creating mp preference", exception);
    }
  }
}
