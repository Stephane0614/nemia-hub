package com.nemia.core.flux.dto;

import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FluxResponse {

  private Long id;
  private LocalDate date;
  private FluxType type;
  private String libelle;
  private BigDecimal montant;
  private FluxCategory categorie;
  private PaymentMode modePaiement;
  private String commentaire;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public FluxResponse() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
