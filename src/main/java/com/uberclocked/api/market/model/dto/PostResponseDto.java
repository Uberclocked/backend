package com.uberclocked.api.market.model.dto;

import com.uberclocked.api.market.model.entity.Post;
import com.uberclocked.api.market.model.entity.PostStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class PostResponseDto {

    private UUID id;
    private String title;
    private byte[] image;
    private String description;
    private Double price;
    private String category;
    private PostStatus status;
    private LocalDateTime createdAt;

    private UUID sellerId;
    private String sellerUserName;

    public static PostResponseDto fromEntity(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.id = post.getId();
        dto.title = post.getTitle();
        dto.image = post.getImage();
        dto.description = post.getDescription();
        dto.price = post.getPrice();
        dto.category = post.getCategory();
        dto.status = post.getStatus();
        dto.createdAt = post.getCreatedAt();

        if (post.getSeller() != null) {
            dto.sellerId = post.getSeller().getId();
            dto.sellerUserName = post.getSeller().getUserName();
        }
        return dto;
    }
}
