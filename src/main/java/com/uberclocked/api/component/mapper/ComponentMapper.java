package com.uberclocked.api.component.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.entity.Component;

@Mapper(componentModel = "spring")
public interface ComponentMapper {
  ComponentDto toDto(Component entity);

  @Mapping(target = "fields", ignore = true)
  Component toEntity(ComponentDto dto);

  @AfterMapping
  default void mapFields(ComponentDto dto, @MappingTarget Component entity) {
    dto.fields().forEach(fieldDto -> entity.addField(
        fieldDto.name(),
        fieldDto.type(),
        fieldDto.required(),
        fieldDto.defaultValue()));
  }
}
