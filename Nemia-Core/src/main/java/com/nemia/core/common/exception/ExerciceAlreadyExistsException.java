package com.nemia.core.common.exception;

public class ExerciceAlreadyExistsException extends RuntimeException {

  public ExerciceAlreadyExistsException(String libelleExercice) {
    super("Un exercice existe déjà avec ce libellé : " + libelleExercice);
  }
}