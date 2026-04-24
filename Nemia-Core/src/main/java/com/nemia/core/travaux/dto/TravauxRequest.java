package com.nemia.core.travaux.dto;

import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.travaux.model.FinalitePressentie;
import com.nemia.core.travaux.model.StatutTravaux;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TravauxRequest {

  @NotBlank(message = "Le libellé des travaux est obligatoire")
  @Size(max = 200, message = "Le libellé ne doit pas dépasser 200 caractères")
  private String libelleTravaux;

  @NotNull(message = "Le bien rattaché est obligatoire")
  private Long bienId;

  private LocalDate dateDebut;
  private LocalDate dateFin;

  @NotNull(message = "Le montant total est obligatoire")
  @Positive(message = "Le montant total doit être positif")
  private BigDecimal montantTotal;

  @NotNull(message = "La finalité pressentie est obligatoire")
  private FinalitePressentie finalitePressentie;

  @NotNull(message = "La qualification pressentie est obligatoire")
  private QualificationPressentie qualificationPressentie;

  private StatutTravaux statutTravaux;

  @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères")
  private String commentaire;

  // Getters & Setters
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
}
