package com.nemia.core.travaux.model;

public enum FinalitePressentie {
  ENTRETIEN_COURANT("Entretien courant"),
  REPARATION("Réparation"),
  AMELIORATION("Amélioration"),
  CREATION("Création"),
  REMISE_EN_ETAT("Remise en état"),
  A_ARBITRER("À arbitrer");

  private final String label;

  FinalitePressentie(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
