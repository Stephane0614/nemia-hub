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
import java.util.ArrayList;
import java.util.List;

public class FluxResponse {

  private Long id;
  private LocalDate date;
  private FluxType type;
  private String libelle;
  private BigDecimal montant;
  private FluxCategory categorie;
  private PaymentMode modePaiement;
  private Long bienId;

  private Long exerciceId;

  private LocalDate dateValeur;

  private Occurrence occurrence;

  private StatutJustificatif statutJustificatif;

  private QualificationPressentie qualificationPressentie;

  private StatutTraitement statutTraitement;

  private String commentaire;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<String> warnings = new ArrayList<>();

  private Long travauxId;

  private Long mobilierId;

  private Long empruntId;

  private Long justificatifId;

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

  public Long getBienId() {
    return bienId;
  }

  public void setBienId(Long bienId) {
    this.bienId = bienId;
  }

  public Long getExerciceId() {
    return exerciceId;
  }

  public void setExerciceId(Long exerciceId) {
    this.exerciceId = exerciceId;
  }

  public LocalDate getDateValeur() {
    return dateValeur;
  }

  public void setDateValeur(LocalDate dateValeur) {
    this.dateValeur = dateValeur;
  }

  public Occurrence getOccurrence() {
    return occurrence;
  }

  public void setOccurrence(Occurrence occurrence) {
    this.occurrence = occurrence;
  }

  public StatutJustificatif getStatutJustificatif() {
    return statutJustificatif;
  }

  public void setStatutJustificatif(StatutJustificatif statutJustificatif) {
    this.statutJustificatif = statutJustificatif;
  }

  public QualificationPressentie getQualificationPressentie() {
    return qualificationPressentie;
  }

  public void setQualificationPressentie(QualificationPressentie qualificationPressentie) {
    this.qualificationPressentie = qualificationPressentie;
  }

  public StatutTraitement getStatutTraitement() {
    return statutTraitement;
  }

  public void setStatutTraitement(StatutTraitement statutTraitement) {
    this.statutTraitement = statutTraitement;
  }

  public List<String> getWarnings() {
    return warnings;
  }

  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }

  public Long getTravauxId() {
    return travauxId;
  }

  public void setTravauxId(Long travauxId) {
    this.travauxId = travauxId;
  }

  public Long getMobilierId() {
    return mobilierId;
  }

  public void setMobilierId(Long mobilierId) {
    this.mobilierId = mobilierId;
  }

  public Long getEmpruntId() {
    return empruntId;
  }

  public void setEmpruntId(Long empruntId) {
    this.empruntId = empruntId;
  }

  public Long getJustificatifId() {
    return justificatifId;
  }

  public void setJustificatifId(Long justificatifId) {
    this.justificatifId = justificatifId;
  }
}
