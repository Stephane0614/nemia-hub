package com.nemia.core.exercice.model;

public enum NiveauCompletude {
  FAIBLE("Faible"),
  MOYEN("Moyen"),
  AVANCE("Avancé"),
  COMPLET("Complet");

  private final String label;

  NiveauCompletude(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
