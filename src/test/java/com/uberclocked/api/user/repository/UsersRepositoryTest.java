package com.uberclocked.api.user.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uberclocked.api.user.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
public class UsersRepositoryTest {

  @Autowired UsersRepository repository;

  @Test
  void findByAuth0Id_whenUserExists_returnsUser() {
    String auth0Id = "auth0|123";

    User user = new User(auth0Id, "santino@mail.com", "Santino");
    repository.save(user);

    assertTrue(repository.findByAuth0Id(auth0Id).isPresent());
  }

  @Test
  void findByAuth0Id_whenUserDoesNotExist_returnsEmpty() {
    assertFalse(repository.findByAuth0Id("auth0|missing").isPresent());
  }
}
