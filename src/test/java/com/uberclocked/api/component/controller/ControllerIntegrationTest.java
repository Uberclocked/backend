package com.uberclocked.api.component.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Test
  void create_whenValid_persistsAndReturns201() throws Exception {
    mockMvc.perform(post("/components")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
              "code": "C1",
              "displayName": "Component 1",
              "fields": [
                {
                  "name": "enabled",
                  "type": "BOOLEAN",
                  "required": true,
                  "defaultValue": null
                }
              ]
            }
                                """))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value("TC"))
        .andExpect(jsonPath("$.displayName").value("Test Component"))
        .andExpect(jsonPath("$.fields").isArray())
        .andExpect(jsonPath("$.fields").isEmpty());
  }
}
