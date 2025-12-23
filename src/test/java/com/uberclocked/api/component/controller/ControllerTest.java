package com.uberclocked.api.component.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.service.ComponentService;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ComponentController.class)
public class ControllerTest {
  @Autowired MockMvc mockMvc;
  @MockitoBean ComponentService service;

  @Test
  void create_whenValid_returns201() throws Exception {
    ComponentDto dto = new ComponentDto("TC", "Test Component", new HashSet<>());

    when(service.create(any())).thenReturn(dto);

    mockMvc.perform(
        post("/components")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                {
                "code": "TC,
                "displayName": "Test Component",
                "fields": []
                }
            """));
  }
}
