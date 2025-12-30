package com.uberclocked.api.component.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.UpdateComponentDto;
import com.uberclocked.api.component.model.dto.field.ComponentFieldDto;
import com.uberclocked.api.component.model.entity.Component;
import com.uberclocked.api.component.model.entity.field.FieldType;
import java.util.Map;
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
    assertEquals(code, dto.skuPrefix());
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
            Map.of("Test Field", new ComponentFieldDto(FieldType.STRING, true, null)));
    Component entity = mapper.toEntity(dto);
    assertEquals(code, entity.getSkuPrefix());
    assertEquals(displayName, entity.getDisplayName());
  }

  @Test
  void update_whenIsValid_modifiesEntity() {
    String fieldName = "UCF";
    ComponentFieldDto fieldDto = new ComponentFieldDto(FieldType.STRING, true, null);
    String name = "Update Test Component";
    UpdateComponentDto updateNameDto = new UpdateComponentDto(name, null);
    Component entity = new Component("TC", "Test Componet");
    mapper.update(updateNameDto, entity);
    UpdateComponentDto updateFieldsDto = new UpdateComponentDto(null, Map.of(fieldName, fieldDto));
    mapper.update(updateFieldsDto, entity);
    assertEquals("TC", entity.getSkuPrefix());
    assertEquals(updateNameDto.displayName(), entity.getDisplayName());
    assertTrue(entity.getFields().containsKey(fieldName));
  }
}
