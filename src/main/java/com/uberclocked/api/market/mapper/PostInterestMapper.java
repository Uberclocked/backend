package com.uberclocked.api.market.mapper;

import com.uberclocked.api.market.model.dto.PostInterestDto;
import com.uberclocked.api.market.model.entity.PostInterest;

public class PostInterestMapper {

  private PostInterestMapper() {}

  public static PostInterestDto toDto(PostInterest interest) {
    PostInterestDto dto = new PostInterestDto();

    dto.setId(interest.getId());
    dto.setUserId(interest.getInterested().getId());
    dto.setUserName(interest.getInterested().getUserName());
    dto.setInfoPurchased(interest.isInfoPurchased());

    return dto;
  }
}
