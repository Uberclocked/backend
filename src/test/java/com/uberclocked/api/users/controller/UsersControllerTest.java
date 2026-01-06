package com.uberclocked.api.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.uberclocked.api.security.TestSecurityConfig;
import com.uberclocked.api.users.User;
import com.uberclocked.api.users.UsersController;
import com.uberclocked.api.users.UsersService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class UsersControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean UsersService usersService;

  @Test
  void createUser_whenValid_returns200_andMapsNullsToEmpty() throws Exception {
    User user = new User("auth0|123", "Santino", "santino@mail.com");

    when(usersService.create(any())).thenReturn(user);

    mockMvc
        .perform(
            post("/me")
                .with(csrf())
                .with(
                    jwt()
                        .jwt(
                            j ->
                                j.subject("auth0|123")
                                    .claim("email", "santino@mail.com")
                                    .claim("name", "Santino")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userName").value("Santino"))
        .andExpect(jsonPath("$.email").value("santino@mail.com"))
        .andExpect(jsonPath("$.country").value(""))
        .andExpect(jsonPath("$.cellPhone").value(""));
  }

  @Test
  void createUser_whenValid_returns200_withCountryAndCellPhone() throws Exception {
    User user = new User("auth0|123", "Santino", "santino@mail.com");
    user.setCountry("AR");
    user.setCellPhone("+54 11 1234-5678");

    when(usersService.create(any())).thenReturn(user);

    mockMvc
        .perform(
            post("/me")
                .with(csrf())
                .with(jwt().jwt(j -> j.subject("auth0|123")))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.country").value("AR"))
        .andExpect(jsonPath("$.cellPhone").value("+54 11 1234-5678"));
  }
}
