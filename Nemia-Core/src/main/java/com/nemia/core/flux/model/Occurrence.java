package com.nemia.core.flux.model;

public enum Occurrence {
  RECURRENT("Récurrent"),
  PONCTUEL("Ponctuel"),
  INDETERMINE("Indéterminé");

  private final String label;

  Occurrence(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
