package com.nemia.core.mobilier.dto;

import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.mobilier.model.CategorieMobilier;
import com.nemia.core.mobilier.model.EtatUsage;
import com.nemia.core.mobilier.model.StatutMobilier;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class MobilierRequest {

  @NotBlank(message = "La désignation est obligatoire")
  @Size(max = 200, message = "La désignation ne doit pas dépasser 200 caractères")
  private String designation;

  @NotNull(message = "Le bien rattaché est obligatoire")
  private Long bienId;

  private LocalDate dateAcquisition;

  @NotNull(message = "Le montant est obligatoire")
  @Positive(message = "Le montant doit être positif")
  private BigDecimal montant;

  @Positive(message = "La quantité doit être positive")
  private Integer quantite;

  @NotNull(message = "La catégorie de mobilier est obligatoire")
  private CategorieMobilier categorieMobilier;

  private EtatUsage etatUsage;

  @NotNull(message = "La qualification pressentie est obligatoire")
  private QualificationPressentie qualificationPressentie;

  @NotNull(message = "Le statut mobilier est obligatoire")
  private StatutMobilier statutMobilier;

  private Long justificatifId;

  @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères")
  private String commentaire;

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
}
