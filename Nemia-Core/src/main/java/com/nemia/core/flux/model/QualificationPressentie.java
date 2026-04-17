package com.nemia.core.flux.model;

public enum QualificationPressentie {
  CHARGE_COURANTE("Charge courante"),
  IMMOBILISATION("Immobilisation"),
  A_ARBITRER("À arbitrer");

  private final String label;

  QualificationPressentie(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
