package com.uberclocked.api.component.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import com.uberclocked.api.component.model.entity.Component;
import com.uberclocked.api.component.model.entity.field.FieldType;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

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
    ComponentDto dto =
        new ComponentDto(
            code,
            displayName,
            Set.of(new ComponentFieldDto("Test field", FieldType.STRING, true, null)));
    Component entity = mapper.toEntity(dto);
    assertEquals(code, entity.getCode());
    assertEquals(displayName, entity.getDisplayName());
  }
}
