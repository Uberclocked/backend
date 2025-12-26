package com.uberclocked.api.component.model.dto.field;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uberclocked.api.component.model.entity.field.FieldType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class ComponentFieldDtoTest {
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void validation_whenDtoIsValid_succeeds() {
    ComponentFieldDto dto = new ComponentFieldDto(FieldType.STRING, true, null);
    assertTrue(validator.validate(dto).isEmpty());
  }

  @Test
  void validation_whenTypeIsNull_fails() {
    ComponentFieldDto dto = new ComponentFieldDto(null, true, null);
    Set<ConstraintViolation<ComponentFieldDto>> violations = validator.validate(dto);
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
  }
}
