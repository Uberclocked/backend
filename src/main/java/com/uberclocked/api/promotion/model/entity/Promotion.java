package com.uberclocked.api.promotion.model.entity;

import com.uberclocked.api.company.model.entity.Company;
import com.uberclocked.api.user.model.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Promotion {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  private String title;
  private String description;

  @Column(nullable = false)
  @Min(1) @Max(100)
  private Integer discount;

  private LocalDateTime startDate;
  private LocalDateTime endDate;

  private Integer maxUses;

  @Column(nullable = false)
  private Integer usedCount = 0;

  @Column(nullable = false)
  private boolean active = true;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  private Company company;

  @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PromotionTarget> targets = new ArrayList<>();

  @Version
  private Long version;
}
