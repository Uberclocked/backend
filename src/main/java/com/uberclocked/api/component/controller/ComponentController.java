package com.uberclocked.api.component.controller;

import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.model.dto.UpdateComponentDto;
import com.uberclocked.api.component.service.ComponentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Components")
@Validated
@RestController
@RequestMapping("/components")
public class ComponentController {
  private final ComponentService componentService;

  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification = "Spring-managed service is injected and intentionally shared")
  public ComponentController(ComponentService service) {
    this.componentService = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ComponentDto create(@Valid @RequestBody ComponentDto dto) {
    return componentService.create(dto);
  }

  @PatchMapping("/{code}")
  @ResponseStatus(HttpStatus.OK)
  public ComponentDto update(
      @RequestBody UpdateComponentDto dto, @PathVariable("code") String code) {
    return componentService.update(dto, code);
  }

  @DeleteMapping("/{code}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("code") String code) {
    componentService.delete(code);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<ComponentDto> getAll() {
    return componentService.getAll();
  }

  @GetMapping("/{code}")
  @ResponseStatus(HttpStatus.OK)
  public ComponentDto getOne(@PathVariable("code") String code) {
    return componentService.getOne(code);
  }
}
