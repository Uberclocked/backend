package com.uberclocked.api.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.uberclocked.api.emailData.AdminConfig;
import com.uberclocked.api.emailData.EmailService;
import com.uberclocked.api.market.service.PostInterestService;
import com.uberclocked.api.market.service.PostService;
import com.uberclocked.api.payment.model.dto.InterestedInfoPaymentDto;
import com.uberclocked.api.payment.model.dto.InterestedInfoPreferenceRequest;
import com.uberclocked.api.user.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.PaymentAdditionalInfoRequest;
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
import com.uberclocked.api.payment.model.dto.PaymentStatus;
import com.uberclocked.api.payment.model.dto.PreferenceDto;
import com.uberclocked.api.payment.repository.MercadoPagoRepository;
import com.uberclocked.api.purchase.model.entity.Purchase;
import com.uberclocked.api.purchase.service.PurchaseService;

@Service
public class MercadoPagoService {

  private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

  private final MercadoPagoRepository mpRepository;
  private final CartService cartService;
  private final PurchaseService purchaseService;
  private final PostInterestService postService;

  private final AdminConfig adminConfig;
  private final EmailService emailService;

  public MercadoPagoService(
          CartService cartService,
          PurchaseService purchaseService,
          MercadoPagoRepository mpRepository,
          PostInterestService postService,
          AdminConfig adminConfig,
          EmailService emailService
  ) {
    this.cartService = cartService;
    this.purchaseService = purchaseService;
    this.mpRepository = mpRepository;
    this.postService = postService;
    this.adminConfig = adminConfig;
    this.emailService = emailService;
  }

  @Transactional
  public PaymentDto createPayment(Jwt jwt, MpBrickSubmitDto body) {
    String userSub = jwt != null ? jwt.getSubject() : "unknown";
    log.info("Creating MercadoPago payment for user: {}", userSub);

    List<PaymentItemRequest> items = new ArrayList<>();

    Purchase myPurchase = purchaseService.createPurchase(jwt);

    try {
      double purchaseTotal = myPurchase.getTotalAmount();
      int itemCount = myPurchase.getCart() != null && myPurchase.getCart().items() != null
              ? myPurchase.getCart().items().size()
              : 0;

      log.info("Purchase created -> purchaseId={}, items={}, totalAmount={}",
              myPurchase.getId(), itemCount, BigDecimal.valueOf(purchaseTotal).setScale(2, RoundingMode.HALF_UP)
      );
    } catch (Exception e) {
      log.warn("Could not log purchase totals", e);
    }

    for (CartItem item : myPurchase.getCart().items()) {
      BigDecimal unit =
              BigDecimal.valueOf(item.totalPrice())
                      .divide(BigDecimal.valueOf(item.quantity()), 2, RoundingMode.HALF_UP);

      items.add(
              PaymentItemRequest.builder()
                      .id(item.id().toString())
                      .title(item.name())
                      .quantity(item.quantity())
                      .unitPrice(unit)
                      .build()
      );
    }

    BigDecimal txAmount = BigDecimal.valueOf(myPurchase.getTotalAmount()).setScale(2, RoundingMode.HALF_UP);

    log.info("MP payment request -> externalReference={}, transactionAmount={}, paymentMethodId={}, installments={}",
            myPurchase.getId(), txAmount, body.paymentMethodId(), body.installments()
    );

    Payment payment = mpRepository.createPayment(
            PaymentCreateRequest.builder()
                    .token(body.token())
                    .additionalInfo(PaymentAdditionalInfoRequest.builder().items(items).build())
                    .paymentMethodId(body.paymentMethodId())
                    .issuerId(body.issuerId())
                    .installments(body.installments())
                    .transactionAmount(txAmount)
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
                    .build()
    );

    log.info("Payment created successfully. Payment ID: {}", payment.getId());

    String subject = "New order received - UberClocked";
    String bodyText =
            "A new order has been received.\n\n" +
                    "Purchase ID: " + myPurchase.getId() + "\n" +
                    "User: " + myPurchase.getUser().getUserName() + "\n" +
                    "Email: " + myPurchase.getUser().getEmail() + "\n" +
                    "Total: $" + myPurchase.getTotalAmount();

    try {
      emailService.sendToMany(adminConfig.getAdminEmails(), subject, bodyText);
    } catch (Exception e) {
      log.warn("Could not send admin notification", e);
    }

    return new PaymentDto(myPurchase.getId(), payment.getId(), PaymentStatus.APPROVED);
  }

  public PreferenceDto createPreference(Jwt jwt) {
    String userSub = jwt != null ? jwt.getSubject() : "unknown";
    log.info("Creating MercadoPago preference for user: {}", userSub);

    List<PreferenceItemRequest> items = new ArrayList<>();
    Cart myCart = cartService.getOrCreateActiveCart(jwt);

    if (myCart.items().isEmpty()) {
      throw new RuntimeException("Can't buy cart with no products");
    }

    double subtotal = cartService.subtotal(myCart);
    double discount = myCart.getDiscountAmount() == null ? 0.0 : myCart.getDiscountAmount();
    double fee = CartService.CHECKOUT_FEE;
    double totalToPay = cartService.totalToPay(myCart);

    log.info("Cart totals -> cartId={}, items={}, subtotal={}, discount={}, fee={}, totalToPay={}, promoCode={}",
            myCart.getId(),
            myCart.items().size(),
            BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP),
            BigDecimal.valueOf(discount).setScale(2, RoundingMode.HALF_UP),
            BigDecimal.valueOf(fee).setScale(2, RoundingMode.HALF_UP),
            BigDecimal.valueOf(totalToPay).setScale(2, RoundingMode.HALF_UP),
            (myCart.getAppliedPromotion() != null ? myCart.getAppliedPromotion().getCode() : null)
    );

    for (CartItem item : myCart.items()) {
      BigDecimal unit =
              BigDecimal.valueOf(item.totalPrice())
                      .divide(BigDecimal.valueOf(item.quantity()), 2, RoundingMode.HALF_UP);

      items.add(
              PreferenceItemRequest.builder()
                      .id(item.id().toString())
                      .title(item.name())
                      .quantity(item.quantity())
                      .currencyId("ARS")
                      .unitPrice(unit)
                      .build()
      );
    }

    BigDecimal itemsSum = items.stream()
            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .setScale(2, RoundingMode.HALF_UP);

    log.info("MP preference items sum (computed) -> {}", itemsSum);

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

    String prefId = mpRepository.createPreference(request).getId();

    log.info("Preference created successfully. Preference ID: {}", prefId);

    return new PreferenceDto(prefId);
  }

  @Transactional
  public PaymentDto createInterestedInfoPayment(Jwt jwt, InterestedInfoPaymentDto body) {
    String userSub = jwt != null ? jwt.getSubject() : "unknown";
    log.info("Creating MercadoPago interested-info payment for user: {}", userSub);

    BigDecimal amount = BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP);
    String externalRef = "INTEREST_INFO:" + body.postId() + ":" + body.interestedUserId();

    log.info("Interested-info payment -> externalRef={}, amount={}", externalRef, amount);

    List<PaymentItemRequest> items = List.of(
            PaymentItemRequest.builder()
                    .id(body.interestedUserId().toString())
                    .title("Interested user contact information")
                    .quantity(1)
                    .unitPrice(amount)
                    .build()
    );

    PaymentPayerRequest payer = PaymentPayerRequest.builder()
            .email(body.payer().email())
            .identification(
                    IdentificationRequest.builder()
                            .type(body.payer().identification().type())
                            .number(body.payer().identification().number())
                            .build()
            )
            .build();

    PaymentCreateRequest request = PaymentCreateRequest.builder()
            .token(body.token())
            .paymentMethodId(body.paymentMethodId())
            .issuerId(body.issuerId())
            .installments(body.installments())
            .transactionAmount(amount)
            .additionalInfo(PaymentAdditionalInfoRequest.builder().items(items).build())
            .payer(payer)
            .externalReference(externalRef)
            .description("Interested info purchase")
            .build();

    Payment payment = mpRepository.createPayment(request);

    log.info("Interested-info payment created successfully. Payment ID: {}", payment.getId());

    String[] parts = externalRef.split(":");
    UUID postId = UUID.fromString(parts[1]);
    UUID interestedUserId = UUID.fromString(parts[2]);
    postService.buyInterestedInfo(postId, interestedUserId, jwt);

    return new PaymentDto(null, payment.getId(), PaymentStatus.APPROVED);
  }

  @Transactional
  public PreferenceDto createInterestedInfoPreference(Jwt jwt, InterestedInfoPreferenceRequest body) {
    String userSub = jwt != null ? jwt.getSubject() : "unknown";
    log.info("Creating MercadoPago interested-info preference for user: {}", userSub);

    BigDecimal unitPrice = BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP);

    List<PreferenceItemRequest> items = List.of(
            PreferenceItemRequest.builder()
                    .id(body.interestedUserId().toString())
                    .title("Interested user contact information")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(unitPrice)
                    .build()
    );

    String externalRef = "INTEREST_INFO:" + body.postId() + ":" + body.interestedUserId();

    log.info("Interested-info preference -> externalRef={}, amount={}", externalRef, unitPrice);

    PreferenceRequest request = PreferenceRequest.builder()
            .items(items)
            .externalReference(externalRef)
            .backUrls(
                    PreferenceBackUrlsRequest.builder()
                            .success("http://localhost:5173/payment/success")
                            .failure("http://localhost:5173/payment/failure")
                            .pending("http://localhost:5173/payment/pending")
                            .build()
            )
            .build();

    String prefId = mpRepository.createPreference(request).getId();

    log.info("Interested-info preference created successfully. Preference ID: {}", prefId);

    return new PreferenceDto(prefId);
  }
}