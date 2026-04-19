package com.nemia.core.common.exception;

public class JustificatifNotFoundException extends RuntimeException {

  public JustificatifNotFoundException(Long id) {
    super("Justificatif introuvable : " + id);
  }
}