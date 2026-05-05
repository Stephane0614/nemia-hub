package com.nemia.core.emprunt.model;

public enum StatutEmprunt {
  EN_COURS("En cours"),
  TERMINE("Terminé"),
  SUSPENDU("Suspendu"),
  A_VERIFIER("À vérifier");

  private final String label;

  StatutEmprunt(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
