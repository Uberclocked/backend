package com.uberclocked.api.component.model.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class ComponentDtoTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validation_whenDtoIsValid_succeeds() {
    ComponentDto dto = new ComponentDto("TC", "Test Name", Set.of());

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty());
  }

  @Test
  void validation_whenCodeIsBlank_fails() {
    ComponentDto dto = new ComponentDto("", "Test Component", Set.of());

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("code")));
  }

  @Test
  void validation_whenDisplayNameIsBlank_fails() {
    ComponentDto dto = new ComponentDto("TC", "", Set.of());

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("displayName")));
  }

  @Test
  void validation_whenDisplayFieldsAreNull_fails() {
    ComponentDto dto = new ComponentDto("TC", "Test Component", null);

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(
        violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("fields")));
  }
}
