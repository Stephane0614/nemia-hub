package com.nemia.core.mobilier.dto;

import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.mobilier.model.CategorieMobilier;
import com.nemia.core.mobilier.model.EtatUsage;
import com.nemia.core.mobilier.model.StatutMobilier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MobilierResponse {

  private Long id;
  private String designation;
  private Long bienId;
  private LocalDate dateAcquisition;
  private BigDecimal montant;
  private Integer quantite;
  private CategorieMobilier categorieMobilier;
  private EtatUsage etatUsage;
  private QualificationPressentie qualificationPressentie;
  private StatutMobilier statutMobilier;
  private Long justificatifId;
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

  public String getDesignation() {
    return designation;
  }

  public void setDesignation(String designation) {
    this.designation = designation;
  }

  public Long getBienId() {
    return bienId;
  }

  public void setBienId(Long bienId) {
    this.bienId = bienId;
  }

  public LocalDate getDateAcquisition() {
    return dateAcquisition;
  }

  public void setDateAcquisition(LocalDate dateAcquisition) {
    this.dateAcquisition = dateAcquisition;
  }

  public BigDecimal getMontant() {
    return montant;
  }

  public void setMontant(BigDecimal montant) {
    this.montant = montant;
  }

  public Integer getQuantite() {
    return quantite;
  }

  public void setQuantite(Integer quantite) {
    this.quantite = quantite;
  }

  public CategorieMobilier getCategorieMobilier() {
    return categorieMobilier;
  }

  public void setCategorieMobilier(CategorieMobilier categorieMobilier) {
    this.categorieMobilier = categorieMobilier;
  }

  public EtatUsage getEtatUsage() {
    return etatUsage;
  }

  public void setEtatUsage(EtatUsage etatUsage) {
    this.etatUsage = etatUsage;
  }

  public QualificationPressentie getQualificationPressentie() {
    return qualificationPressentie;
  }

  public void setQualificationPressentie(QualificationPressentie qualificationPressentie) {
    this.qualificationPressentie = qualificationPressentie;
  }

  public StatutMobilier getStatutMobilier() {
    return statutMobilier;
  }

  public void setStatutMobilier(StatutMobilier statutMobilier) {
    this.statutMobilier = statutMobilier;
  }

  public Long getJustificatifId() {
    return justificatifId;
  }

  public void setJustificatifId(Long justificatifId) {
    this.justificatifId = justificatifId;
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
