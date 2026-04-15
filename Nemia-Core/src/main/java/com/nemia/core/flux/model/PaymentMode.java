package com.nemia.core.flux.model;

public enum PaymentMode {
    VIREMENT("Virement"),
    CARTE("Carte"),
    PRELEVEMENT("Prélèvement"),
    CHEQUE("Chèque"),
    ESPECES("Espèces"),
    AUTRE("Autre");

    private final String label;

    PaymentMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
