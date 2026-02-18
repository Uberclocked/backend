package com.uberclocked.api.wheel.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class WheelDto {
    public static class WheelStatusResponse {
        public boolean canSpin;
        public Instant nextSpinAt;
        public Long secondsRemaining;
    }

    public static class WheelPrizeDto {
        public String label;
        public int discount;
        public List<PromotionTargetBody> targets;
    }

    public static class WheelSpinResponse {
        public boolean canSpin;
        public Instant nextSpinAt;
        public WheelPrizeDto prize;
        public PromotionDto promotion;
    }

    public static class PromotionTargetBody {
        public String kind;
        public String mode;
        public String sku;
        public String componentType;
    }

    public static class PromotionDto {
        public UUID id;
        public String code;
        public Integer discount;
        public UUID userId;
        public Boolean active;
        public Integer maxUses;
    }
}