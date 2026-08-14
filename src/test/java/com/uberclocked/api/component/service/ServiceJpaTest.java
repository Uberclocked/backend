package com.uberclocked.api.component.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uberclocked.api.component.mapper.ComponentMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.UpdateComponentDto;
import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import com.uberclocked.api.component.repository.ComponentRepository;
import java.util.HashMap;
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
  @Autowired ComponentService service;
  @Autowired ComponentRepository repository;

  @Test
  void create_persistsEntity() {
    String skuPrefix = "TC";
    ComponentDto dto = new ComponentDto(skuPrefix, "Test Component", Map.of());
    service.create(dto);
    assertTrue(repository.existsBySkuPrefix(skuPrefix));
  }

  @Test
  void update_modifiesEntity() {
    String skuPrefix = "TC";
    String name = "UTC";
    Map<String, ComponentFieldDto> fields = new HashMap<>();
    UpdateComponentDto updateDto = new UpdateComponentDto(name, fields);
    ComponentDto createDto = new ComponentDto(skuPrefix, name, fields);
    service.create(createDto);
    ComponentDto resultDto = service.update(updateDto, skuPrefix);
    assertEquals(skuPrefix, resultDto.skuPrefix());
    assertEquals(updateDto.displayName(), resultDto.displayName());
    assertEquals(updateDto.fields(), resultDto.fields());
  }

  @Test
  void delete_removesEntity() {
    String skuPrefix = "TC";
    ComponentDto dto = new ComponentDto(skuPrefix, "Test Component", Map.of());
    service.create(dto);
    service.delete(skuPrefix);
    assertFalse(repository.existsBySkuPrefix(skuPrefix));
  }
}
