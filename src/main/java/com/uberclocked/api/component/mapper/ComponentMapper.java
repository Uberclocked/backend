package com.uberclocked.api.component.mapper;

import org.mapstruct.Mapper;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.entity.Component;

@Mapper(componentModel = "spring")
public interface ComponentMapper {
  ComponentDto toDto(Component entity);

  Component toEntity(ComponentDto dto);
}
