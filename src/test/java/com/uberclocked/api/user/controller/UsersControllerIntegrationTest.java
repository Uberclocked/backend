package com.uberclocked.api.user.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.repository.UsersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsersControllerIntegrationTest {

  @Autowired MockMvc mockMvc;

  @Autowired UsersRepository usersRepository;

  @AfterEach
  void cleanup() {
    usersRepository.deleteAll();
  }

  @Test
  void createUser_whenNew_persistsAndReturns200() throws Exception {
    mockMvc
        .perform(
            post("/me")
                .with(csrf())
                .with(
                    jwt()
                        .jwt(
                            j ->
                                j.subject("auth0|new")
                                    .claim("email", "new@mail.com")
                                    .claim("name", "New User")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.userName").value("New User"))
        .andExpect(jsonPath("$.email").value("new@mail.com"));

    org.junit.jupiter.api.Assertions.assertEquals(1, usersRepository.count());
    org.junit.jupiter.api.Assertions.assertTrue(
        usersRepository.findByAuth0Id("auth0|new").isPresent());
  }

  @Test
  void createUser_whenAlreadyExists_doesNotDuplicate() throws Exception {
    mockMvc
        .perform(
            post("/me")
                .with(csrf())
                .with(
                    jwt()
                        .jwt(
                            j ->
                                j.subject("auth0|same")
                                    .claim("email", "same@mail.com")
                                    .claim("name", "Same User")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/me")
                .with(csrf())
                .with(
                    jwt()
                        .jwt(
                            j ->
                                j.subject("auth0|same")
                                    .claim("email", "same@mail.com")
                                    .claim("name", "Same User")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    org.junit.jupiter.api.Assertions.assertEquals(1, usersRepository.count());
  }

  @Test
  void updateUser_whenExists_updatesOnlyProvidedFields_andReturns200_PATCH() throws Exception {
    User user = new User("auth0|me", "Old Name", "old@mail.com");
    user.setCountry("AR");
    user.setCellPhone("111");
    usersRepository.save(user);

    mockMvc
        .perform(
            patch("/me")
                .with(csrf())
                .with(jwt().jwt(j -> j.subject("auth0|me")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                            {
                                              "country": "UY"
                                            }
                                            """))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.country").value("UY"))
        .andExpect(jsonPath("$.userName").value("Old Name"))
        .andExpect(jsonPath("$.email").value("old@mail.com"))
        .andExpect(jsonPath("$.cellPhone").value("111"));

    User updated = usersRepository.findByAuth0Id("auth0|me").orElseThrow();
    Assertions.assertEquals("UY", updated.getCountry());
    Assertions.assertEquals("Old Name", updated.getUserName());
    Assertions.assertEquals("old@mail.com", updated.getEmail());
    Assertions.assertEquals("111", updated.getCellPhone());
  }

  @Test
  void updateUser_whenExists_updatesOnlyProvidedFields_andReturns200_PUT() throws Exception {

    User user = new User("auth0|me", "Old Name", "old@mail.com");
    user.setCountry("AR");
    user.setCellPhone("111");
    usersRepository.save(user);

    mockMvc
        .perform(
            patch("/me")
                .with(csrf())
                .with(jwt().jwt(j -> j.subject("auth0|me")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                            {
                                              "userName": "New Name",
                                              "email": "new@mail.com",
                                              "country": "BR",
                                              "cellPhone": "222"
                                            }
                                            """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userName").value("New Name"))
        .andExpect(jsonPath("$.email").value("new@mail.com"))
        .andExpect(jsonPath("$.country").value("BR"))
        .andExpect(jsonPath("$.cellPhone").value("222"));

    User updated = usersRepository.findByAuth0Id("auth0|me").orElseThrow();
    Assertions.assertEquals("New Name", updated.getUserName());
    Assertions.assertEquals("new@mail.com", updated.getEmail());
    Assertions.assertEquals("BR", updated.getCountry());
    Assertions.assertEquals("222", updated.getCellPhone());
  }

  @Test
  void deleteUser_whenExists_deletesAndReturns204() throws Exception {
    User user = new User("auth0|me", "Old Name", "old@mail.com");
    user.setCountry("AR");
    usersRepository.save(user);

    mockMvc
        .perform(delete("/me").with(csrf()).with(jwt().jwt(j -> j.subject("auth0|me"))))
        .andExpect(status().isNoContent());

    Assertions.assertEquals(0, usersRepository.count());
    Assertions.assertTrue(usersRepository.findByAuth0Id("auth0|me").isEmpty());
  }
}
