package com.uberclocked.api.users.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.uberclocked.api.users.User;
import com.uberclocked.api.users.UsersRepository;
import com.uberclocked.api.users.UsersService;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

  @Mock UsersRepository repository;

  @InjectMocks UsersService service;

  private Jwt jwt(String sub, String email, String name) {
    return new Jwt(
        "token",
        Instant.now(),
        Instant.now().plusSeconds(3600),
        Map.of("alg", "none"),
        Map.of("sub", sub, "email", email, "name", name));
  }

  @Test
  void create_whenAuth0IdExists_returnsExisting_andDoesNotSave() {
    String auth0Id = "auth0|existing";
    Jwt jwt = jwt(auth0Id, "existing@mail.com", "Existing");

    User existingUser = new User(auth0Id, "existing@mail.com", "Existing");

    when(repository.findByAuth0Id(auth0Id)).thenReturn(Optional.of(existingUser));

    User result = service.create(jwt);

    assertEquals(existingUser, result);
    verify(repository, never()).save(any());
  }

  @Test
  void create_whenAuth0IdDoesNotExist_savesNewUser_andSetsLastLogin() {
    String auth0Id = "auth0|new";
    Jwt jwt = jwt(auth0Id, "new@mail.com", "New User");

    when(repository.findByAuth0Id(auth0Id)).thenReturn(Optional.empty());

    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User result = service.create(jwt);

    verify(repository).save(captor.capture());
    User saved = captor.getValue();

    assertNotNull(result);
    assertEquals(auth0Id, saved.getAuth0Id());
    assertEquals("new@mail.com", saved.getEmail());
    assertEquals("New User", saved.getUserName());
    assertNotNull(saved.getLastLogin());
  }
}
