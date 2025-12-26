package com.uberclocked.api.component.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import com.uberclocked.api.component.model.entity.field.FieldType;
import java.util.Map;
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

  @Autowired
  MockMvc mockMvc;

  @Test
  void create_whenValid_persistsAndReturns201() throws Exception {
    String fieldName = "Test Field";
    ComponentFieldDto fieldDto = new ComponentFieldDto(FieldType.STRING, false, null);
    ComponentDto dto = new ComponentDto("TC", "Test Component", Map.of(fieldName, fieldDto));

    String requestBody = new ObjectMapper().writeValueAsString(dto);

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
        .andExpect(jsonPath("$.fields").isMap())
        .andExpect(jsonPath("$.fields['Test Field']").exists())
        .andExpect(jsonPath("$.fields['Test Field'].type").value("STRING"))
        .andExpect(jsonPath("$.fields['Test Field'].required").value(false))
        .andExpect(jsonPath("$.fields['Test Field'].defaultValue").doesNotExist());
  }

  @Test
  void delete_whenValid_removesAndReturns204() throws Exception {
    String code = "TC";
    ComponentDto dto = new ComponentDto(code, "Test Component", Map.of());

    String requestBody = new ObjectMapper().writeValueAsString(dto);
    mockMvc
        .perform(
            post("/components")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

    mockMvc.perform(delete("/components/" + code))
        .andExpect(status().isNoContent());
  }
}
