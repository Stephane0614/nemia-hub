package com.nemia.core.mobilier.model;

public enum StatutMobilier {
  BRUT("Brut"),
  QUALIFIE("Qualifié"),
  A_REVOIR("À revoir"),
  VALIDE("Validé");

  private final String label;

  StatutMobilier(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
