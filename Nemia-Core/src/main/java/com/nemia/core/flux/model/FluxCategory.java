package com.nemia.core.flux.model;

public enum FluxCategory {
  // Recettes
  LOYER("Loyer"),
  CHARGES_REFACTUREES("Charges refacturées"),
  INDEMNITE_RECUE("Indemnité reçue"),
  AUTRE_RECETTE_EXPLOITATION("Autre recette d'exploitation"),

  // Charges courantes
  ELECTRICITE("Électricité"),
  EAU("Eau"),
  INTERNET("Internet"),
  ASSURANCE("Assurance"),
  TAXE("Taxe"),
  COPROPRIETE("Copropriété"),
  FRAIS_BANCAIRES("Frais bancaires"),
  HONORAIRES("Honoraires"),
  ENTRETIEN_COURANT("Entretien courant"),
  CONSOMMABLES("Consommables"),
  MENAGE("Ménage"),
  FOURNITURES("Fournitures"),
  AUTRE_CHARGE_EXPLOITATION("Autre charge d'exploitation"),

  // Travaux / immobilisations
  TRAVAUX("Travaux"),
  REPARATION_IMPORTANTE("Réparation importante"),
  AMELIORATION("Amélioration"),
  MOBILIER("Mobilier"),
  ELECTROMENAGER("Électroménager"),
  EQUIPEMENT("Équipement"),
  DECORATION("Décoration"),

  // Financement
  FRAIS_FINANCEMENT("Frais de financement"),
  EMPRUNT_INTERETS("Emprunt — intérêts"),
  EMPRUNT_ASSURANCE("Emprunt — assurance"),
  EMPRUNT_CAPITAL("Emprunt — capital"),

  // Mouvements financiers
  APPORT("Apport"),
  RETRAIT("Retrait"),
  VIREMENT_INTERNE("Virement interne"),

  // Divers
  REGULARISATION("Régularisation"),
  AUTRE("Autre");

  private final String label;

  FluxCategory(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
