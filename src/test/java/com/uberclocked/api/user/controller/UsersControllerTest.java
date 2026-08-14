package com.uberclocked.api.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.uberclocked.api.security.TestSecurityConfig;
import com.uberclocked.api.user.mapper.UserMapper;
import com.uberclocked.api.user.model.dto.UserDataDto;
import com.uberclocked.api.user.model.entity.User;
import com.uberclocked.api.user.service.UsersService;
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
  @MockitoBean UserMapper userMapper;

  @Test
  void createUser_whenValid_returns200_andMapsNullsToEmpty() throws Exception {
    User user = new User("auth0|123", "Santino", "santino@mail.com");

    when(usersService.getUserOrCreate(any())).thenReturn(user);
    when(userMapper.toDto(any()))
        .thenReturn(new UserDataDto("Santino", "santino@mail.com", "", ""));

    mockMvc
        .perform(
            post("/me")
                .with(csrf())
                .with(jwt().jwt(j -> j.subject("auth0|123")))
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

    when(usersService.getUserOrCreate(any())).thenReturn(user);
    when(userMapper.toDto(any()))
        .thenReturn(new UserDataDto("Santino", "santino@mail.com", "AR", "+54 11 1234-5678"));

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

  @Test
  void updateUser_whenValid_returns200_andMapsNullsToEmpty_PATCH() throws Exception {
    User updated = new User("auth0|123", "Santino", "santino@mail.com");

    when(usersService.updateData(any(), any())).thenReturn(updated);
    when(userMapper.toDto(any()))
        .thenReturn(new UserDataDto("Santino", "santino@mail.com", "", ""));

    mockMvc
        .perform(
            patch("/me")
                .with(csrf())
                .with(jwt().jwt(j -> j.subject("auth0|123")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                    { "country": "AR" }
                                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userName").value("Santino"))
        .andExpect(jsonPath("$.email").value("santino@mail.com"))
        .andExpect(jsonPath("$.country").value(""))
        .andExpect(jsonPath("$.cellPhone").value(""));
  }

  @Test
  void updateUser_whenValid_returns200_withCountryAndCellPhone_PATCH() throws Exception {
    User updated = new User("auth0|123", "Santino", "santino@mail.com");
    updated.setCountry("AR");
    updated.setCellPhone("+54 11 1234-5678");

    when(usersService.updateData(any(), any())).thenReturn(updated);
    when(userMapper.toDto(any()))
        .thenReturn(new UserDataDto("Santino", "santino@mail.com", "AR", "+54 11 1234-5678"));

    mockMvc
        .perform(
            patch("/me")
                .with(csrf())
                .with(jwt().jwt(j -> j.subject("auth0|123")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                                    {
                                      "userName": "Santino",
                                      "email": "santino@mail.com",
                                      "country": "AR",
                                      "cellPhone": "+54 11 1234-5678"
                                    }
                                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.country").value("AR"))
        .andExpect(jsonPath("$.cellPhone").value("+54 11 1234-5678"));
  }

  @Test
  void deleteUser_whenExists_returns204_andCallsService() throws Exception {
    doNothing().when(usersService).delete(any());

    mockMvc
        .perform(delete("/me").with(csrf()).with(jwt().jwt(j -> j.subject("auth0|123"))))
        .andExpect(status().isNoContent());

    verify(usersService).delete(any());
  }
}
