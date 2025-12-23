package com.uberclocked.api.component.repository;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.uberclocked.api.component.model.entity.Component;

@DataJpaTest
public class RepositoryTest {
  @Autowired
  ComponentRepository repository;

  @Test
  void existsByCode_whenComponentExists_returnsTrue() {
    String code = "TC";
    Component component = new Component(code, "Test Component");
    repository.save(component);
    assertTrue(repository.existsByCode(code));
  }
}
