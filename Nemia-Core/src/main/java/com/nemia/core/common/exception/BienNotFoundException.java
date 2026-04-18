package com.nemia.core.common.exception;

public class BienNotFoundException extends RuntimeException {

  public BienNotFoundException(Long id) {
    super("Bien introuvable : " + id);
  }
}
