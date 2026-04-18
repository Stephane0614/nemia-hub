package com.nemia.core.exercice.model;

public enum StatutExercice {
  OUVERT("Ouvert"),
  EN_PREPARATION_DE_CLOTURE("En préparation de clôture"),
  CLOTURE("Clôturé");

  private final String label;

  StatutExercice(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}