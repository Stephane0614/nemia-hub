package com.nemia.core.travaux.dto;

import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.travaux.model.FinalitePressentie;
import com.nemia.core.travaux.model.StatutTravaux;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TravauxResponse {

  private Long id;
  private String libelleTravaux;
  private Long bienId;
  private LocalDate dateDebut;
  private LocalDate dateFin;
  private BigDecimal montantTotal;
  private FinalitePressentie finalitePressentie;
  private QualificationPressentie qualificationPressentie;
  private StatutTravaux statutTravaux;
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

  public String getLibelleTravaux() {
    return libelleTravaux;
  }

  public void setLibelleTravaux(String libelleTravaux) {
    this.libelleTravaux = libelleTravaux;
  }

  public Long getBienId() {
    return bienId;
  }

  public void setBienId(Long bienId) {
    this.bienId = bienId;
  }

  public LocalDate getDateDebut() {
    return dateDebut;
  }

  public void setDateDebut(LocalDate dateDebut) {
    this.dateDebut = dateDebut;
  }

  public LocalDate getDateFin() {
    return dateFin;
  }

  public void setDateFin(LocalDate dateFin) {
    this.dateFin = dateFin;
  }

  public BigDecimal getMontantTotal() {
    return montantTotal;
  }

  public void setMontantTotal(BigDecimal montantTotal) {
    this.montantTotal = montantTotal;
  }

  public FinalitePressentie getFinalitePressentie() {
    return finalitePressentie;
  }

  public void setFinalitePressentie(FinalitePressentie finalitePressentie) {
    this.finalitePressentie = finalitePressentie;
  }

  public QualificationPressentie getQualificationPressentie() {
    return qualificationPressentie;
  }

  public void setQualificationPressentie(QualificationPressentie qualificationPressentie) {
    this.qualificationPressentie = qualificationPressentie;
  }

  public StatutTravaux getStatutTravaux() {
    return statutTravaux;
  }

  public void setStatutTravaux(StatutTravaux statutTravaux) {
    this.statutTravaux = statutTravaux;
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
