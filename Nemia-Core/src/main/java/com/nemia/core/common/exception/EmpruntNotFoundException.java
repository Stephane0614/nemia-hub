package com.nemia.core.common.exception;

public class EmpruntNotFoundException extends RuntimeException {

  public EmpruntNotFoundException(Long id) {
    super("Emprunt introuvable : " + id);
  }
}
