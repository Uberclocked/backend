package com.uberclocked.api.users.controller;

import com.uberclocked.api.users.mapper.UserMapper;
import com.uberclocked.api.users.model.dto.UserDataDto;
import com.uberclocked.api.users.model.entity.User;
import com.uberclocked.api.users.service.UsersService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
public class UsersController {

  private final UsersService usersService;
  private final UserMapper mapper;

  public UsersController(UsersService usersService, UserMapper mapper) {
    this.usersService = usersService;
    this.mapper = mapper;
  }

  @PostMapping()
  public UserDataDto createUser(@AuthenticationPrincipal Jwt jwt) {
    User user = usersService.create(jwt);
    return mapper.toDto(user);
  }

  @PatchMapping
  public UserDataDto modifyUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserDataDto dto) {
    User user = usersService.updateData(jwt, dto);
    return mapper.toDto(user);
  }
}
