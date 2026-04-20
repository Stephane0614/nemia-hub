package com.nemia.core.justificatif.model;

public enum StatutDocumentaire {
  A_FOURNIR("À fournir"),
  FOURNI("Fourni"),
  INCOMPLET("Incomplet"),
  A_VERIFIER("À vérifier"),
  REJETE("Rejeté");

  private final String label;

  StatutDocumentaire(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
