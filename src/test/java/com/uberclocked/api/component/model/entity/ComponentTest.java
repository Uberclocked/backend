package com.uberclocked.api.component.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uberclocked.api.component.model.entity.field.FieldType;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class ComponentTest {

  @Test
  void addField_whenFieldIsNotAdded_addsFieldToComponent() {
    Component component = new Component("TC", "Test Component");

    String fieldName = "Test Field";
    component.addField(fieldName, FieldType.STRING, true, null);

    assertEquals(1, component.getFields().size());
    assertTrue(component.getFields().containsKey(fieldName));
  }

  @Test
  void addField_whenFieldIsAdded_throwsException() {
    Component component = new Component("TC", "Test Component");

    String fieldName = "Test Field";
    component.addField(fieldName, FieldType.STRING, true, null);

    assertThrows(
        IllegalArgumentException.class,
        () -> component.addField(fieldName, FieldType.STRING, true, null));
  }

  @Test
  void removeField_whenFieldNameExists_removesFieldAndReturnsIt() {
    Component component = new Component("TC", "Test Component");
    String testFieldName = "Test Field";

    ComponentField addedField = component.addField(testFieldName, FieldType.STRING, true, null);

    ComponentField removedField = component.removeField(testFieldName);

    assertEquals(0, component.getFields().size());
    assertEquals(addedField, removedField);
  }

  @Test
  void removeField_whenFieldNameDoesNotExist_throwsException() {
    Component component = new Component("TC", "Test Component");

    assertThrows(NoSuchElementException.class, () -> component.removeField("Missing Field"));
  }
}
