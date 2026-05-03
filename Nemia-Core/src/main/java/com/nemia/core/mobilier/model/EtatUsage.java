package com.nemia.core.mobilier.model;

public enum EtatUsage {

    NEUF("Neuf"),
    OCCASION("Occasion"),
    REMPLACEMENT("Remplacement"),
    INDETERMINE("Indéterminé");

    private final String label;

    EtatUsage(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}