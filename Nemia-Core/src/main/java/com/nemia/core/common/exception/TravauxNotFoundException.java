package com.nemia.core.common.exception;

public class TravauxNotFoundException extends RuntimeException {

  public TravauxNotFoundException(Long id) {
    super("Travaux introuvable : " + id);
  }
}
