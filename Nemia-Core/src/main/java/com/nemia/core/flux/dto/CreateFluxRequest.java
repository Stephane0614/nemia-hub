package com.nemia.core.flux.dto;

import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.Occurrence;
import com.nemia.core.flux.model.PaymentMode;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateFluxRequest {

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

  private Long bienId;

  private Long exerciceId;

  private LocalDate dateValeur;

  private Occurrence occurrence;

  private StatutJustificatif statutJustificatif;

  private QualificationPressentie qualificationPressentie;

  private StatutTraitement statutTraitement;

  @Size(max = 500)
  private String commentaire;

  public CreateFluxRequest() {}

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
}
