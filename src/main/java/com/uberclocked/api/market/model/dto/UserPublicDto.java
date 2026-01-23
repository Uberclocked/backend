package com.uberclocked.api.market.model.dto;

import com.uberclocked.api.user.model.entity.User;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserPublicDto {

  private UUID id;
  private String email;
  private String cellPhone;
  private String userName;

  public static UserPublicDto fromEntity(User user) {
    UserPublicDto dto = new UserPublicDto();
    dto.id = user.getId();
    dto.email = user.getEmail();
    dto.cellPhone = user.getCellPhone();
    dto.userName = user.getUserName();
    return dto;
  }
}
