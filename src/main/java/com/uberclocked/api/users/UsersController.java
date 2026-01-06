package com.uberclocked.api.users;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class UsersController {

  private final UsersService usersService;

  public UsersController(UsersService usersService) {
    this.usersService = usersService;
  }

  @PostMapping()
  public UserDataDto createUser(@AuthenticationPrincipal Jwt jwt) {

    User user = usersService.create(jwt);
    return new UserDataDto(
        user.getUserName(),
        user.getEmail(),
        user.getCountry() == null ? "" : user.getCountry(),
        user.getCellPhone() == null ? "" : user.getCellPhone());
  }
}
