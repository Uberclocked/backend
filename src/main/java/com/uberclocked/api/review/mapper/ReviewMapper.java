package com.uberclocked.api.review.mapper;

import com.uberclocked.api.review.model.dto.ReviewResponseDto;
import com.uberclocked.api.review.model.entity.Review;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponseDto toDto(Review r) {
        return new ReviewResponseDto(
                r.getId(),
                r.getProduct().getSkuPrefix(),
                r.getUser().getId(),
                r.getUser().getUserName(),
                r.getQualification(),
                r.getMessage(),
                r.getCreatedAt()
        );
    }

    public List<ReviewResponseDto> toDtoList(List<Review> reviews) {
        return reviews.stream().map(this::toDto).toList();
    }
}
