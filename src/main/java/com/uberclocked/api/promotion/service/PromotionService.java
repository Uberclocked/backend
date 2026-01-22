package com.uberclocked.api.promotion.service;

import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.users.model.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PromotionService {

    public boolean canApplyPromotion(User user, Promotion promo) {
        return promo.getEndDate().isBefore(LocalDateTime.now());
    }

    public void applyPromotion(User user, Promotion promo) {
        if (!canApplyPromotion(user, promo)) {
            throw new IllegalStateException("Promotion expired");
        }
    }
}
