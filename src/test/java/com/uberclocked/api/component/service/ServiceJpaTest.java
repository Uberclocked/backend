package com.uberclocked.api.component.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.uberclocked.api.component.mapper.ComponentMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.entity.Component;
import com.uberclocked.api.component.repository.ComponentRepository;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataJpaTest
@Import(ComponentService.class)
public class ServiceJpaTest {
  @Autowired ComponentService service;
  @Autowired ComponentRepository repository;
  @MockitoBean ComponentMapper mapper;

  @Test
  void create_persistsEntity() {
    String code = "TC";
    ComponentDto dto = new ComponentDto(code, "Test Component", new HashSet<>());
    when(mapper.toEntity(dto)).thenReturn(new Component(dto.code(), dto.displayName()));

    service.create(dto);

    assertTrue(repository.existsByCode(code));
  }
}
