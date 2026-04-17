package com.nemia.core.common.exception;

public class FluxNotFoundException extends RuntimeException {

  public FluxNotFoundException(Long id) {
    super("Flux introuvable avec l'id : " + id);
  }
}
