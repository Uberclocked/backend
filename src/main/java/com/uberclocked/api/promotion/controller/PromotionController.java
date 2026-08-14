package com.uberclocked.api.promotion.controller;

import com.uberclocked.api.cart.model.entity.Cart;
import com.uberclocked.api.cart.model.entity.CartStatus;
import com.uberclocked.api.cart.repository.CartRepository;
import com.uberclocked.api.company.service.CompanyService;
import com.uberclocked.api.promotion.model.dto.PromotionCreateRequest;
import com.uberclocked.api.promotion.model.dto.PromotionDto;
import com.uberclocked.api.promotion.model.dto.PromotionTargetDto;
import com.uberclocked.api.promotion.model.dto.PromotionUpdateRequest;
import com.uberclocked.api.promotion.model.entity.Promotion;
import com.uberclocked.api.promotion.model.entity.PromotionTarget;
import com.uberclocked.api.promotion.repository.PromotionRepository;
import com.uberclocked.api.promotion.service.PromotionService;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/promotions")
@RestController
public class PromotionController {

    private final PromotionRepository promotionRepository;
    private final PromotionService promotionService;
    private final UsersService usersService;
    private final CompanyService companyService;
    private final CartRepository cartRepository;

    public PromotionController(
            PromotionRepository promotionRepository,
            UsersService usersService,
            CompanyService companyService,
            PromotionService promotionService,
            CartRepository cartRepository
    ) {
        this.promotionRepository = promotionRepository;
        this.usersService = usersService;
        this.companyService = companyService;
        this.promotionService = promotionService;
        this.cartRepository = cartRepository;
    }

    @PreAuthorize("hasRole('Admin')")
    @PostMapping
    public PromotionDto create(@RequestBody PromotionCreateRequest req) {
        Promotion p = new Promotion();
        p.setId(UUID.randomUUID());
        p.setCode(req.code().trim());
        p.setTitle(req.title());
        p.setDescription(req.description());
        p.setDiscount(req.discount());
        p.setStartDate(req.startDate());
        p.setEndDate(req.endDate());

        p.setActive(req.active() == null || req.active());
        p.setMaxUses(req.maxUses());

        if (req.userId() != null) {
            p.setUser(usersService.getUSerById(req.userId()));
        }
        if (req.companyId() != null) {
            p.setCompany(companyService.getCompany(req.companyId()));
        }

        if (req.targets() != null) {
            for (var tr : req.targets()) {
                PromotionTarget t = new PromotionTarget();
                t.setPromotion(p);
                t.setKind(tr.kind());
                t.setMode(tr.mode() == null ? PromotionTarget.TargetMode.INCLUDE : tr.mode());
                t.setSku(tr.sku());
                t.setComponentType(tr.componentType());
                p.getTargets().add(t);
            }
        }

        Promotion saved = promotionRepository.save(p);
        return toDto(saved);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping
    public List<PromotionDto> list() {
        return promotionRepository.findAll().stream().map(this::toDto).toList();
    }

    @PreAuthorize("hasRole('Admin')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        promotionRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('Admin')")
    @PatchMapping("/{id}")
    public PromotionDto update(@PathVariable UUID id, @RequestBody PromotionUpdateRequest req) {
        Promotion p = promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found: " + id));

        if (req.code() != null) p.setCode(req.code().trim());
        if (req.title() != null) p.setTitle(req.title());
        if (req.description() != null) p.setDescription(req.description());
        if (req.discount() != null) p.setDiscount(req.discount());
        if (req.startDate() != null) p.setStartDate(req.startDate());
        if (req.endDate() != null) p.setEndDate(req.endDate());

        if (req.active() != null) p.setActive(req.active());
        if (req.maxUses() != null) p.setMaxUses(req.maxUses());

        if (req.userId() != null) p.setUser(usersService.getUSerById(req.userId()));
        if (req.companyId() != null) p.setCompany(companyService.getCompany(req.companyId()));

        if (req.targets() != null) {
            p.getTargets().clear();
            for (var tr : req.targets()) {
                PromotionTarget t = new PromotionTarget();
                t.setPromotion(p);
                t.setKind(tr.kind());
                t.setMode(tr.mode() == null ? PromotionTarget.TargetMode.INCLUDE : tr.mode());
                t.setSku(tr.sku());
                t.setComponentType(tr.componentType());
                p.getTargets().add(t);
            }
        }

        Promotion saved = promotionRepository.save(p);
        return toDto(saved);
    }

    @GetMapping("/applicable")
    public List<PromotionDto> applicable(@AuthenticationPrincipal Jwt jwt) {
        User user = usersService.getUserOrCreate(jwt);

        Cart cart = cartRepository.findByUserAndStatus(user, CartStatus.ACTIVE)
                .orElse(null);

        List<Promotion> all = promotionRepository.findAll();

        List<Promotion> applicable = (cart == null)
                ? all.stream().filter(Promotion::isActive).toList()
                : promotionService.getApplicablePromotions(user.getId(), cart, all);

        return applicable.stream().map(this::toDto).toList();
    }

    private PromotionDto toDto(Promotion p) {
        var targets = p.getTargets() == null ? List.<PromotionTargetDto>of()
                : p.getTargets().stream()
                .map(t -> new PromotionTargetDto(
                        t.getId(), t.getKind(), t.getSku(), t.getComponentType(), t.getMode()
                ))
                .toList();

        return new PromotionDto(
                p.getId(),
                p.getCode(),
                p.getTitle(),
                p.getDescription(),
                p.getDiscount(),
                p.getStartDate(),
                p.getEndDate(),
                p.isActive(),
                p.getMaxUses(),
                p.getUsedCount(),
                p.getUser() != null ? p.getUser().getId() : null,
                p.getCompany() != null ? p.getCompany().getId() : null,
                targets
        );
    }
}