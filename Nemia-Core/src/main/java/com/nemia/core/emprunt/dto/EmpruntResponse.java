package com.nemia.core.emprunt.dto;

import com.nemia.core.emprunt.model.StatutEmprunt;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmpruntResponse {

  private Long id;
  private String referencePret;
  private Long bienId;
  private String organismePreteur;
  private BigDecimal mensualiteTotale;
  private LocalDate datePremiereEcheance;
  private LocalDate dateDerniereEcheance;
  private StatutEmprunt statutEmprunt;
  private String commentaire;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Getters & Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReferencePret() {
    return referencePret;
  }

  public void setReferencePret(String referencePret) {
    this.referencePret = referencePret;
  }

  public Long getBienId() {
    return bienId;
  }

  public void setBienId(Long bienId) {
    this.bienId = bienId;
  }

  public String getOrganismePreteur() {
    return organismePreteur;
  }

  public void setOrganismePreteur(String organismePreteur) {
    this.organismePreteur = organismePreteur;
  }

  public BigDecimal getMensualiteTotale() {
    return mensualiteTotale;
  }

  public void setMensualiteTotale(BigDecimal mensualiteTotale) {
    this.mensualiteTotale = mensualiteTotale;
  }

  public LocalDate getDatePremiereEcheance() {
    return datePremiereEcheance;
  }

  public void setDatePremiereEcheance(LocalDate datePremiereEcheance) {
    this.datePremiereEcheance = datePremiereEcheance;
  }

  public LocalDate getDateDerniereEcheance() {
    return dateDerniereEcheance;
  }

  public void setDateDerniereEcheance(LocalDate dateDerniereEcheance) {
    this.dateDerniereEcheance = dateDerniereEcheance;
  }

  public StatutEmprunt getStatutEmprunt() {
    return statutEmprunt;
  }

  public void setStatutEmprunt(StatutEmprunt statutEmprunt) {
    this.statutEmprunt = statutEmprunt;
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
