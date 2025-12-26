package com.uberclocked.api.component.mapper;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.UpdateComponentDto;
import com.uberclocked.api.component.model.entity.Component;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ComponentMapper {
  ComponentDto toDto(Component entity);

  @Mapping(target = "fields", ignore = true)
  Component toEntity(ComponentDto dto);

  @AfterMapping
  default void mapCreateFields(ComponentDto dto, @MappingTarget Component entity) {
    dto.fields().forEach(
        (key, value) -> entity.addField(
            key,
            value.type(),
            value.required(),
            value.defaultValue()));
  }

  @Mapping(target = "displayName", ignore = true)
  @Mapping(target = "fields", ignore = true)
  void update(UpdateComponentDto dto, @MappingTarget Component entity);

  @AfterMapping
  default void patch(UpdateComponentDto dto, @MappingTarget Component entity) {
    dto.displayName().ifPresent(entity::setDisplayName);

    if (dto.fields().isPresent()) {
      entity.clearFields();
      entity.getFields().keySet().forEach(entity::removeField);
      dto.fields().get().forEach(
          (key, value) -> entity.addField(
              key,
              value.type(),
              value.required(),
              value.defaultValue()));
    }
  }
}
