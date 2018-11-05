package com.hedvig.botService.enteties;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  private static final long serialVersionUID = -4573187319651527625L;

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
