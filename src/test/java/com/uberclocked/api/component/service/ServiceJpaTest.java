package com.uberclocked.api.component.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uberclocked.api.component.mapper.ComponentMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.repository.ComponentRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@DataJpaTest
@ComponentScan(basePackageClasses = ComponentMapper.class)
@Import(ComponentService.class)
class ServiceJpaTest {
  @Autowired
  ComponentService service;
  @Autowired
  ComponentRepository repository;

  @Test
  void create_persistsEntity() {
    String code = "TC";
    ComponentDto dto = new ComponentDto(code, "Test Component", Map.of());
    service.create(dto);
    assertTrue(repository.existsByCode(code));
  }

  @Test
  void delete_removesEntity() {
    String code = "TC";
    ComponentDto dto = new ComponentDto(code, "Test Component", Map.of());
    service.create(dto);
    service.delete(code);
    assertFalse(repository.existsByCode(code));
  }
}
