package com.nemia.core.mobilier.model;

public enum CategorieMobilier {
  LITERIE("Literie"),
  ELECTROMENAGER("Électroménager"),
  MEUBLE("Meuble"),
  EQUIPEMENT("Équipement"),
  DECORATION("Décoration"),
  AUTRE("Autre");

  private final String label;

  CategorieMobilier(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
