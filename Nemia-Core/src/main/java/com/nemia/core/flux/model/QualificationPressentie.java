package com.nemia.core.flux.model;

public enum QualificationPressentie {
  CHARGE_COURANTE("Charge courante"),
  IMMOBILISATION("Immobilisation"),
  MIXTE_OU_A_VENTILER("Mixte ou à ventiler"),
  HORS_RESULTAT("Hors résultat"),
  A_ARBITRER("À arbitrer"),
  NON_APPLICABLE("Non applicable");

  private final String label;

  QualificationPressentie(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
