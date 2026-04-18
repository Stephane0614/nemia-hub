package com.nemia.core.bien.model;

public enum TypeLocation {
  LMNP_LONGUE_DUREE("LMNP longue durée"),
  LMNP_COURTE_DUREE("LMNP courte durée"),
  MIXTE("Mixte"),
  AUTRE("Autre");

  private final String label;

  TypeLocation(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}