package com.uberclocked.api.component.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.entity.Component;

public class MapperTest {
  private final ComponentMapper mapper = Mappers.getMapper(ComponentMapper.class);

  @Test
  void toDto_whenEntityProvided_mapsAllFields() {
    String code = "code";
    String displayName = "display name";
    Component entity = new Component(code, displayName);
    ComponentDto dto = mapper.toDto(entity);
    assertEquals(code, dto.code());
    assertEquals(displayName, dto.displayName());
  }

  @Test
  void toEntity_whenDtoProvided_mapsAllFields() {
    String code = "code";
    String displayName = "display name";
    ComponentDto dto = new ComponentDto(code, displayName, new HashSet<>());
    Component entity = mapper.toEntity(dto);
    assertEquals(code, entity.getCode());
    assertEquals(displayName, entity.getDisplayName());
  }
}
