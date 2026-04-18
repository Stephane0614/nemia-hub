package com.nemia.core.common.exception;

public class ExerciceNotFoundException extends RuntimeException {

  public ExerciceNotFoundException(Long id) {
    super("Exercice introuvable : " + id);
  }
}
