package com.nemia.core.flux.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.PaymentMode;

public class UpdateFluxRequest {

    @NotNull
    private LocalDate date;

    @NotNull
    private FluxType type;

    @NotBlank
    @Size(max = 120)
    private String libelle;

    @NotNull
    @Positive
    private BigDecimal montant;

    @NotNull
    private FluxCategory categorie;

    @NotNull
    private PaymentMode modePaiement;

    @Size(max = 500)
    private String commentaire;

    public UpdateFluxRequest() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public FluxType getType() {
        return type;
    }

    public void setType(FluxType type) {
        this.type = type;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public FluxCategory getCategorie() {
        return categorie;
    }

    public void setCategorie(FluxCategory categorie) {
        this.categorie = categorie;
    }

    public PaymentMode getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(PaymentMode modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}