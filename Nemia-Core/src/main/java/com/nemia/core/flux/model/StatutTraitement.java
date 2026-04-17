package com.nemia.core.flux.model;

public enum StatutTraitement {
  BRUT("Brut"),
  QUALIFIE("Qualifié"),
  A_REVOIR("À revoir"),
  VALIDE("Validé");

  private final String label;

  StatutTraitement(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}