package com.uberclocked.api.common.exceptions;

public class ResourceDoesNotExistsException extends RuntimeException {
  public ResourceDoesNotExistsException(String message) {
    super(message);
  }
}
