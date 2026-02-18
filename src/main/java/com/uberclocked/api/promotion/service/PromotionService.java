package com.uberclocked.api.promotion.service;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartItem;
import com.uberclocked.api.company.service.CompanyUserService;
import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.promotion.model.entity.PromotionTarget;
import com.uberclocked.api.promotion.repository.PromotionRepository;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.uberclocked.api.wheel.model.dto.WheelDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

  private final CompanyUserService companyUserService;
  private final UsersService userService;
  private final PromotionRepository promotionRepository;

  public PromotionService(CompanyUserService companyUserService, UsersService userService, PromotionRepository promotionRepository) {
    this.companyUserService = companyUserService;
    this.userService = userService;
    this.promotionRepository = promotionRepository;
  }

  public boolean canApplyPromotion(UUID userId, Promotion promo, Cart cart) {
    LocalDateTime now = LocalDateTime.now();

    if (promo == null) return false;
    if (!promo.isActive()) return false;

    if (promo.getStartDate() != null && promo.getStartDate().isAfter(now)) return false;
    if (promo.getEndDate() != null && promo.getEndDate().isBefore(now)) return false;

    if (promo.getUser() != null && !promo.getUser().getId().equals(userId)) return false;

    if (promo.getMaxUses() != null && promo.getUsedCount() >= promo.getMaxUses()) return false;

    if (promo.getCompany() != null) {
      User user = userService.getUSerById(userId);
      if (!companyUserService.isUserInCompany(user, promo.getCompany())) return false;
    }

    return appliesToCart(promo, cart);
  }

  public void assertCanApply(UUID userId, Promotion promo, Cart cart) {
    if (!canApplyPromotion(userId, promo, cart)) {
      throw new IllegalStateException("Promotion not applicable");
    }
  }

  public List<Promotion> getApplicablePromotions(UUID userId, Cart cart, List<Promotion> allPromos) {
    return allPromos.stream()
            .filter(promo -> canApplyPromotion(userId, promo, cart))
            .toList();
  }
  public void applyPromotion(UUID userId, Promotion promo, Cart cart) {
    assertCanApply(userId, promo, cart);
  }

  @Transactional
  public void consumePromotion(UUID promotionId) {
    Promotion promo = promotionRepository.findById(promotionId)
            .orElseThrow(() -> new IllegalStateException("Promotion not found: " + promotionId));

    if (!promo.isActive()) throw new IllegalStateException("Promotion not active");

    Integer used = promo.getUsedCount() == null ? 0 : promo.getUsedCount();
    Integer max = promo.getMaxUses();

    if (max != null && used >= max) {
      throw new IllegalStateException("Promotion is exhausted");
    }

    used = used + 1;
    promo.setUsedCount(used);

    if (max != null && used >= max) {
      promo.setActive(false);
    }

    promotionRepository.save(promo);
  }

  private boolean matchesTarget(PromotionTarget t, CartItem item) {
    if (t == null || item == null) return false;

    return switch (t.getKind()) {
      case PRODUCT_SKU -> {
        yield item.getProduct() != null
                && t.getSku() != null
                && t.getSku().equalsIgnoreCase(item.getProduct().getSkuPrefix());
      }
      case COMPONENT_TYPE -> {
        if (t.getComponentType() == null) yield false;
        var comps = item.getComponents();
        yield comps != null && comps.containsKey(t.getComponentType());
      }
      case COMPONENT_SKU -> {
        if (t.getSku() == null) yield false;
        var comps = item.getComponents();
        yield comps != null && comps.values().stream().anyMatch(v -> v.equalsIgnoreCase(t.getSku()));
      }
    };
  }

  private boolean matchesAny(List<PromotionTarget> targets, Cart cart) {
    if (targets == null || targets.isEmpty() || cart == null || cart.getItems() == null) return false;

    return cart.getItems().stream().anyMatch(item ->
            targets.stream().anyMatch(t -> matchesTarget(t, item))
    );
  }

  public boolean appliesToItem(Promotion promo, CartItem item) {
    var targets = promo.getTargets();
    if (targets == null || targets.isEmpty()) return true;

    var includes = targets.stream()
            .filter(t -> t.getMode() == PromotionTarget.TargetMode.INCLUDE)
            .toList();

    var excludes = targets.stream()
            .filter(t -> t.getMode() == PromotionTarget.TargetMode.EXCLUDE)
            .toList();

    boolean excluded = excludes.stream().anyMatch(t -> matchesTarget(t, item));
    if (excluded) return false;

    if (!includes.isEmpty()) {
      return includes.stream().anyMatch(t -> matchesTarget(t, item));
    }

    return true;
  }

  public boolean appliesToCart(Promotion promo, Cart cart) {
    var targets = promo.getTargets();
    if (targets == null || targets.isEmpty()) return true;

    var includes = targets.stream()
            .filter(t -> t.getMode() == PromotionTarget.TargetMode.INCLUDE)
            .toList();

    var excludes = targets.stream()
            .filter(t -> t.getMode() == PromotionTarget.TargetMode.EXCLUDE)
            .toList();

    if (!excludes.isEmpty() && matchesAny(excludes, cart)) return false;
    if (!includes.isEmpty()) return matchesAny(includes, cart);

    return true;
  }

  @Transactional
  public WheelDto.PromotionDto createWheelPromotion(
          String userId,
          String prizeLabel,
          int discount,
          List<WheelDto.PromotionTargetBody> targets
  ) {
    String code = "WHEEL-" + UUID.randomUUID().toString().replace("-", "")
            .substring(0, 8).toUpperCase();

    Promotion p = new Promotion();
    p.setCode(code);
    p.setDiscount(discount);
    p.setTitle("Daily Spin");
    p.setDescription("Prize: " + prizeLabel);
    p.setActive(true);
    p.setMaxUses(1);
    p.setEndDate(LocalDateTime.now().plusDays(1));
    p.setStartDate(LocalDateTime.now());

    User user = userService.getUSerById(userId);
    p.setUser(user);

    if (targets != null && !targets.isEmpty()) {
      for (var t : targets) {
        PromotionTarget pt = new PromotionTarget();
        pt.setPromotion(p);
        pt.setKind(PromotionTarget.TargetKind.valueOf(t.kind));
        pt.setMode(PromotionTarget.TargetMode.valueOf(t.mode));
        pt.setSku(t.sku);
        pt.setComponentType(t.componentType);
        p.getTargets().add(pt);
      }
    }

    Promotion saved = promotionRepository.save(p);

    var dto = new WheelDto.PromotionDto();
    dto.id = saved.getId();
    dto.code = saved.getCode();
    dto.discount = saved.getDiscount();
    dto.userId = saved.getUser().getId();
    dto.active = saved.isActive();
    dto.maxUses = saved.getMaxUses();
    return dto;
  }
}
