package com.uberclocked.api.promotion.service;

import com.uberclocked.api.company.service.CompanyUserService;
import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

  private final CompanyUserService companyUserService;
  private final UsersService userService;

  public PromotionService(CompanyUserService companyUserService, UsersService userService) {
    this.companyUserService = companyUserService;
    this.userService = userService;
  }

  public boolean canApplyPromotion(UUID id, Promotion promo) {
    LocalDateTime now = LocalDateTime.now();

    if (promo.getStartDate() != null && promo.getStartDate().isAfter(now)) {
      return false;
    }
    if (promo.getEndDate() != null && promo.getEndDate().isBefore(now)) {
      return false;
    }
    if (promo.getUser() != null && !promo.getUser().getId().equals(id)) {
      return false;
    }
    if (promo.getCompany() != null) {
      User user = userService.getUSerById(id);
      return companyUserService.isUserInCompany(user, promo.getCompany());
    }
    return true;
  }

  public void applyPromotion(UUID id, Promotion promo) {
    if (!canApplyPromotion(id, promo)) {
      throw new IllegalStateException("Promotion expired");
    }
  }

  public List<Promotion> getApplicablePromotions(UUID user, List<Promotion> allPromos) {
    return allPromos.stream().filter(promo -> canApplyPromotion(user, promo)).toList();
  }
}
