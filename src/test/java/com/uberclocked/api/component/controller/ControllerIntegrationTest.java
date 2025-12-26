package com.uberclocked.api.component.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ControllerIntegrationTest {

  @Autowired MockMvc mockMvc;

  @Test
  void create_whenValid_persistsAndReturns201() throws Exception {
    String requestBody =
        """
        {
          "code": "TC",
          "displayName": "Test Component",
          "fields": [
            {
              "name": "Test Field",
              "type": "STRING",
              "required": true,
              "defaultValue": null
            }]}
        """;

    mockMvc
        .perform(
            post("/components")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("TC"))
        .andExpect(jsonPath("$.displayName").value("Test Component"))
        .andExpect(jsonPath("$.fields").isArray())
        .andExpect(jsonPath("$.fields").isNotEmpty());
  }
}
