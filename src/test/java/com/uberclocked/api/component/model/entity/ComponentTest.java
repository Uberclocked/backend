package com.uberclocked.api.component.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uberclocked.api.component.model.entity.field.FieldType;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class ComponentTest {

  @Test
  void addField_whenValidInput_addsFieldToComponent() {
    Component component = new Component("TC", "Test Component");

    ComponentField newField = component.addField("Test Field", FieldType.STRING, true, null);

    assertEquals(1, component.getFields().size());
    assertTrue(component.getFields().contains(newField));
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
