package com.uberclocked.api.users.service;

import static org.junit.jupiter.api.Assertions.*;

import com.uberclocked.api.users.User;
import com.uberclocked.api.users.UsersRepository;
import com.uberclocked.api.users.UsersService;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.Jwt;

@DataJpaTest
@Import(UsersService.class)
class UsersServiceJpaTest {

  @Autowired UsersService service;
  @Autowired UsersRepository repository;

  private Jwt jwt(String sub, String email, String name) {
    return new Jwt(
        "token",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("sub", sub, "email", email, "name", name));
  }

  @Test
  void create_persistsEntity_whenNew() {
    String auth0Id = "auth0|new";
    Jwt jwt = jwt(auth0Id, "new@mail.com", "New User");

    User created = service.create(jwt);

    assertNotNull(created);
    assertEquals(1, repository.count());
    assertTrue(repository.findByAuth0Id(auth0Id).isPresent());
    assertNotNull(repository.findByAuth0Id(auth0Id).get().getLastLogin());
  }

  @Test
  void create_returnsExisting_whenAlreadyExists_andDoesNotDuplicate() {
    String auth0Id = "auth0|same";
    Jwt jwt = jwt(auth0Id, "same@mail.com", "Same User");

    service.create(jwt);
    assertEquals(1, repository.count());

    service.create(jwt);
    assertEquals(1, repository.count()); // clave: no duplica
  }
}
