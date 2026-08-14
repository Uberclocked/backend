package com.uberclocked.api.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class ResourceDoesNotExistsException extends RuntimeException {
  public ResourceDoesNotExistsException(String message) {
    super(message);
  }
}
