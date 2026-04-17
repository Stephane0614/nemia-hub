package com.nemia.core.flux.model;

public enum Occurrence {
  PONCTUEL("Ponctuel"),
  RECURRENT("Récurrent"),
  EXCEPTIONNEL("Exceptionnel");

  private final String label;

  Occurrence(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }
}
