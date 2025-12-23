package com.uberclocked.api.component.service;

import org.springframework.stereotype.Service;

import com.uberclocked.api.common.exceptions.ResourceAlreadyExistsException;
import com.uberclocked.api.component.mapper.ComponentMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.repository.ComponentRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public final class ComponentService {
  private ComponentRepository repository;
  private ComponentMapper mapper;

  public ComponentDto create(ComponentDto dto) {
    if (repository.existByCode(dto.code())) {
      throw new ResourceAlreadyExistsException(
          "Component with code '" + dto.code() + "' already exists.");
    }
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
  }
}
