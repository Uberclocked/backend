package com.uberclocked.api.component.model.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ComponentDtoTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validation_whenDtoIsValid_succeeds() {
    ComponentDto dto = new ComponentDto("TC", "Test Name", Map.of());

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty());
  }

  @Test
  void validation_whenCodeIsBlank_fails() {
    ComponentDto dto = new ComponentDto("", "Test Component", Map.of());

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("skuPrefix")));
  }

  @Test
  void validation_whenDisplayNameIsBlank_fails() {
    ComponentDto dto = new ComponentDto("TC", "", Map.of());

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("displayName")));
  }

  @Test
  void validation_whenDisplayFieldsAreNull_fails() {
    ComponentDto dto = new ComponentDto("TC", "Test Component", null);

    Set<ConstraintViolation<ComponentDto>> violations = validator.validate(dto);

    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fields")));
  }
}
