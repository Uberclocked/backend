package com.uberclocked.api.user.controller;

import com.uberclocked.api.user.mapper.UserMapper;
import com.uberclocked.api.user.model.dto.UserDataDto;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

  @GetMapping
  public UserDataDto getMe(@AuthenticationPrincipal Jwt jwt) {
    User user = usersService.getUserOrCreate(jwt);
    return mapper.toDto(user);

  }

  @PatchMapping
  public UserDataDto modifyUser(@AuthenticationPrincipal Jwt jwt, @RequestBody UserDataDto dto) {
    User user = usersService.updateData(jwt, dto);
    return mapper.toDto(user);
  }

  @DeleteMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@AuthenticationPrincipal Jwt jwt) {
    usersService.delete(jwt);
  }
}
