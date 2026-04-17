package com.nemia.core.flux.model;

public enum FluxCategory {
  LOYER("Loyer"),
  CHARGES_COPRO("Charges copropriété"),
  ELECTRICITE("Électricité"),
  EAU("Eau"),
  INTERNET("Internet"),
  ASSURANCE("Assurance"),
  INTERETS_EMPRUNT("Intérêts d'emprunt"),
  TRAVAUX("Travaux"),
  MOBILIER("Mobilier"),
  FRAIS_BANCAIRES("Frais bancaires"),
  TAXE_FONCIERE("Taxe foncière"),
  HONORAIRES("Honoraires"),
  CONSOMMABLES("Consommable"),
  AUTRE("Autre");

  private final String label;

  FluxCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
