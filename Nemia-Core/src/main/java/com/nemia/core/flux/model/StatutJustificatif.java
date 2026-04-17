package com.nemia.core.flux.model;

public enum StatutJustificatif {
  FOURNI("Fourni"),
  MANQUANT("Manquant"),
  NON_REQUIS("Non requis"),
  A_VERIFIER("À vérifier");

  private final String label;

  StatutJustificatif(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
