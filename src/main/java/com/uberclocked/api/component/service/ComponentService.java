package com.uberclocked.api.component.service;

import com.uberclocked.api.common.exceptions.ResourceAlreadyExistsException;
import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.component.mapper.ComponentMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.UpdateComponentDto;
import com.uberclocked.api.component.model.entity.Component;
import com.uberclocked.api.component.repository.ComponentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ComponentService {
  private ComponentRepository repository;
  private ComponentMapper mapper;

  public ComponentService(ComponentRepository repository, ComponentMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  public ComponentDto create(ComponentDto dto) {
    if (repository.existsBySkuPrefix(dto.skuPrefix())) {
      throw new ResourceAlreadyExistsException(
          "Component with code '" + dto.skuPrefix() + "' already exists.");
    }
    return mapper.toDto(repository.save(mapper.toEntity(dto)));
  }

  public ComponentDto update(UpdateComponentDto dto, String code) {
    if (!repository.existsBySkuPrefix(code)) {
      throw new ResourceDoesNotExistsException(
          "Component with code '" + code + "' does not exists.");
    }
    Component entity = repository.getReferenceById(code);
    mapper.update(dto, entity);
    return mapper.toDto(entity);
  }

  public void delete(String code) {
    if (!repository.existsBySkuPrefix(code)) {
      throw new ResourceDoesNotExistsException(
          "Component with code '" + code + "' does not exists.");
    }
    repository.deleteById(code);
  }

  public Component getEntityById(String skuPrefix) {
    return repository
        .findById(skuPrefix)
        .orElseThrow(
            () ->
                new ResourceDoesNotExistsException(
                    "Component with code '" + skuPrefix + "' does not exists."));
  }

  public boolean exists(String skuPrefix) {
    return repository.existsBySkuPrefix(skuPrefix);
  }

  public java.util.List<ComponentDto> getAll() {
    return repository.findAll()
            .stream()
            .map(mapper::toDto)
            .toList();
  }

  public ComponentDto getOne(String code) {
    if (!repository.existsBySkuPrefix(code)) {
      throw new ResourceDoesNotExistsException(
              "Component with code '" + code + "' does not exists.");
    }

    Component entity = repository.getReferenceById(code);
    return mapper.toDto(entity);
  }
}
