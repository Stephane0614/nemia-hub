package com.nemia.core.flux.model;

public enum StatutJustificatif {
  NON_REQUIS("Non requis"),
  A_FOURNIR("À fournir"),
  FOURNI("Fourni"),
  INCOMPLET("Incomplet"),
  A_VERIFIER("À vérifier"),
  REJETE("Rejeté");

  private final String label;

  StatutJustificatif(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}