package com.uberclocked.api.users.service;

import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.users.mapper.UserMapper;
import com.uberclocked.api.users.model.dto.UserDataDto;
import com.uberclocked.api.users.model.entity.User;
import com.uberclocked.api.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

  private final UsersRepository usersRepository;
  private final UserMapper mapper;

  public UsersService(UsersRepository usersRepository, UserMapper mapper) {
    this.usersRepository = usersRepository;
    this.mapper = mapper;
  }

  private User create(Jwt jwt) {
    String auth0Id = jwt.getSubject();
    User user = usersRepository.findByAuth0Id(auth0Id).orElse(null);
    if (user != null) {
      user.setLastLogin(LocalDateTime.now());
      return usersRepository.save(user);
    }
    String email = jwt.getClaimAsString("https://uberclocked.com/email");
    String name = jwt.getClaimAsString("https://uberclocked.com/name");
    User newUser = new User(auth0Id, name, email);
    newUser.setLastLogin(LocalDateTime.now());
    return usersRepository.save(newUser);
  }

  public User getUserOrCreate(Jwt jwt) {
    String userId = jwt.getSubject();
    User user = usersRepository.findByAuth0Id(userId).orElse(null);
    if (user != null) {
      user.setLastLogin(LocalDateTime.now());
      return usersRepository.save(user);
    }
    return create(jwt);
  }

  public User getUSerById(UUID userId) {
    User user = usersRepository.findById(userId).orElse(null);
    if (user != null) {
      return user;
    }
    throw new ResourceDoesNotExistsException("User does not exists.");
  }

  public User updateData(Jwt jwt, UserDataDto dataDto) {
    User user =
        usersRepository
            .findByAuth0Id(jwt.getSubject())
            .orElseThrow(() -> new ResourceDoesNotExistsException("User does not exists."));

    mapper.update(dataDto, user);
    return usersRepository.save(user);
  }

  @Transactional
  public void delete(Jwt jwt) {
    String auth0Id = jwt.getSubject();
    boolean exists = usersRepository.findByAuth0Id(auth0Id).isPresent();
    if (!exists) {
      throw new ResourceDoesNotExistsException("User does not exist.");
    }
    usersRepository.deleteByAuth0Id(auth0Id);
  }
}
