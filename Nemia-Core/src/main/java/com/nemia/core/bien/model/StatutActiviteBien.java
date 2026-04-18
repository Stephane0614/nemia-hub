package com.nemia.core.bien.model;

public enum StatutActiviteBien {
  EN_PREPARATION("En préparation"),
  ACTIF("Actif"),
  SUSPENDU("Suspendu"),
  CLOTURE("Clôturé");

  private final String label;

  StatutActiviteBien(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
