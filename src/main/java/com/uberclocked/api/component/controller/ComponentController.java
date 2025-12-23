package com.uberclocked.api.component.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.service.ComponentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Components")
@Validated
@RestController
@RequestMapping("/components")
public class ComponentController {
  private ComponentService service;

  public ComponentController(ComponentService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ComponentDto create(
      @Valid @RequestBody ComponentDto dto) {
    return service.create(dto);
  }
}
