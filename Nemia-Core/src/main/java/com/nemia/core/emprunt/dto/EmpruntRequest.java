package com.nemia.core.emprunt.dto;

import com.nemia.core.emprunt.model.StatutEmprunt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EmpruntRequest {

  @NotBlank(message = "La référence du prêt est obligatoire")
  @Size(max = 100, message = "La référence du prêt ne doit pas dépasser 100 caractères")
  private String referencePret;

  @NotNull(message = "Le bien rattaché est obligatoire")
  private Long bienId;

  @Size(max = 150, message = "L'organisme prêteur ne doit pas dépasser 150 caractères")
  private String organismePreteur;

  @Positive(message = "La mensualité totale doit être positive")
  private BigDecimal mensualiteTotale;

  private LocalDate datePremiereEcheance;

  private LocalDate dateDerniereEcheance;

  @NotNull(message = "Le statut de l'emprunt est obligatoire")
  private StatutEmprunt statutEmprunt;

  @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères")
  private String commentaire;

  // Getters & Setters
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
}
