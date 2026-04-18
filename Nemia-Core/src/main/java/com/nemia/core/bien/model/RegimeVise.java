package com.nemia.core.bien.model;

public enum RegimeVise {
  MICRO_BIC("Micro-BIC"),
  REEL("Réel"),
  A_DEFINIR("À définir");

  private final String label;

  RegimeVise(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}