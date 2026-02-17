package com.uberclocked.api.market.model.dto;

import com.uberclocked.api.market.model.entity.PostInterest;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostInterestDto {

  private UUID id;
  private UUID userId;
  private String userName;
  private boolean infoPurchased;

  public static PostInterestDto fromEntity(PostInterest interest) {
    PostInterestDto dto = new PostInterestDto();
    dto.id = interest.getId();
    dto.userName = interest.getInterested().getUserName();
    dto.infoPurchased = interest.isInfoPurchased();
    return dto;
  }
}
