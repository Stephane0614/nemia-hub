package com.nemia.core.justificatif.model;

public enum TypePiece {
  FACTURE("Facture"),
  TICKET("Ticket"),
  RELEVE("Relevé"),
  ECHEANCIER("Échéancier"),
  ACTE("Acte"),
  DEVIS("Devis"),
  AUTRE("Autre");

  private final String label;

  TypePiece(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}