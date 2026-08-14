package com.uberclocked.api.promotion.service;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.cart.model.entity.CartStatus;
import com.uberclocked.api.cart.repository.CartRepository;
import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.promotion.repository.PromotionRepository;
import com.uberclocked.api.user.service.UsersService;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class CartPromotionService {
    private final CartRepository cartRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionService promotionService;
    private final UsersService usersService;

    public CartPromotionService(
            CartRepository cartRepository,
            PromotionRepository promotionRepository,
            PromotionService promotionService,
            UsersService usersService
    ) {
        this.cartRepository = cartRepository;
        this.promotionRepository = promotionRepository;
        this.promotionService = promotionService;
        this.usersService = usersService;
    }

    public Cart applyCoupon(Jwt jwt, String code) {
        var user = usersService.getUserOrCreate(jwt);
        var cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow();

        Promotion promo = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        promotionService.assertCanApply(user.getId(), promo, cart);

        double eligibleSubtotal = cart.getItems().stream()
                .filter(item -> promotionService.appliesToItem(promo, item))
                .mapToDouble(CartItem::getTotalPrice)
                .sum();

        if (eligibleSubtotal <= 0) {
            throw new IllegalStateException("Coupon does not apply to any item in the cart");
        }

        double discount = eligibleSubtotal * (promo.getDiscount() / 100.0);

        cart.setAppliedPromotion(promo);
        cart.setDiscountAmount(discount);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }

    public Cart removeCoupon(Jwt jwt) {
        var user = usersService.getUserOrCreate(jwt);
        var cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE).orElseThrow();

        cart.setAppliedPromotion(null);
        cart.setDiscountAmount(null);
        cart.setUpdatedAt(LocalDateTime.now());
        return cartRepository.save(cart);
    }
}