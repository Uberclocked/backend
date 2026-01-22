package com.uberclocked.api.promotion.service;

import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.users.model.entity.User;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

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
