package com.nemia.core.flux.model;


public enum FluxType {
    RECETTE("Recette"),
    DEPENSE("Dépense");

    private final String label;

    FluxType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}