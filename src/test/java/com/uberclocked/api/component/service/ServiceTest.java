package com.uberclocked.api.component.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uberclocked.api.common.exceptions.ResourceAlreadyExistsException;
import com.uberclocked.api.common.exceptions.ResourceDoesNotExistsException;
import com.uberclocked.api.component.mapper.ComponentMapper;
import com.uberclocked.api.component.model.dto.ComponentDto;
import com.uberclocked.api.component.repository.ComponentRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {
  @Mock ComponentRepository repository;

  @Mock ComponentMapper mapper;

  @InjectMocks ComponentService service;

  @Test
  void create_whenCodeExists_throwsException() {
    String code = "TC";
    ComponentDto dto = new ComponentDto(code, "Test Component", Map.of());

    when(repository.existsBySkuPrefix(code)).thenReturn(true);

    assertThrows(ResourceAlreadyExistsException.class, () -> service.create(dto));
  }

  @Test
  void delete_whenCodeDoesntExists_throwsException() {
    String skuPrefix = "TC";

    when(repository.existsBySkuPrefix(skuPrefix)).thenReturn(false);

    assertThrows(ResourceDoesNotExistsException.class, () -> service.delete(skuPrefix));
    verify(repository, never()).deleteById(any());
  }
}
