package com.uberclocked.api.users.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.uberclocked.api.users.UsersRepository;
import org.junit.jupiter.api.AfterEach;
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
}
