package com.uberclocked.api.component.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import com.uberclocked.api.component.model.entity.field.FieldType;
import com.uberclocked.api.component.service.ComponentService;
import com.uberclocked.api.security.TestSecurityConfig;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ComponentController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class ControllerTest {
  @Autowired MockMvc mockMvc;
  @MockitoBean ComponentService service;

  @Test
  void create_whenValid_returns201() throws Exception {
    String fieldName = "Test Field";
    ComponentFieldDto fieldDto = new ComponentFieldDto(FieldType.STRING, false, null);
    ComponentDto dto = new ComponentDto("TC", "Test Component", Map.of(fieldName, fieldDto));

    when(service.create(dto)).thenReturn(dto);

    String requestBody = new ObjectMapper().writeValueAsString(dto);

    mockMvc
        .perform(post("/components").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.skuPrefix").value("TC"))
        .andExpect(jsonPath("$.displayName").value("Test Component"))
        .andExpect(jsonPath("$.fields").isMap())
        .andExpect(jsonPath("$.fields['Test Field']").exists())
        .andExpect(jsonPath("$.fields['Test Field'].type").value("STRING"))
        .andExpect(jsonPath("$.fields['Test Field'].required").value(false))
        .andExpect(jsonPath("$.fields['Test Field'].defaultValue").doesNotExist());
  }

  @Test
  void update_whenValid_returs200() throws Exception {
    String code = "TC";
    String displayName = "Test Component";
    ComponentDto dto = new ComponentDto(code, displayName, Map.of());

    when(service.update(any(), any())).thenReturn(dto);

    String requestBody = new ObjectMapper().writeValueAsString(dto);

    mockMvc
        .perform(
            patch("/components/" + code)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.skuPrefix").value("TC"))
        .andExpect(jsonPath("$.displayName").value("Test Component"))
        .andExpect(jsonPath("$.fields").isMap());
  }

  @Test
  void delete_whenValid_returns204() throws Exception {
    String code = "TC";

    doNothing().when(service).delete(code);

    mockMvc
        .perform(delete("/components/" + code).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }
}
