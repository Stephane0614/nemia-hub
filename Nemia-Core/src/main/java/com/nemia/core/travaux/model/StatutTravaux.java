package com.nemia.core.travaux.model;

public enum StatutTravaux {
  BRUT("Brut"),
  QUALIFIE("Qualifié"),
  A_REVOIR("À revoir"),
  VALIDE("Validé");

  private final String label;

  StatutTravaux(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
