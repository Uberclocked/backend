package com.uberclocked.api.component.service;

import org.springframework.stereotype.Service;

import com.uberclocked.api.component.model.dto.ComponentDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public final class ComponentService {
  public ComponentDto create(ComponentDto dto) {
    // TODO
    throw new UnsupportedOperationException("Not implemented yet.");
  }
}
