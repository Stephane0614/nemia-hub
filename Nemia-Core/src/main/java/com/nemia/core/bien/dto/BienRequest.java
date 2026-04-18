package com.nemia.core.bien.dto;

import com.nemia.core.bien.model.RegimeVise;
import com.nemia.core.bien.model.StatutActiviteBien;
import com.nemia.core.bien.model.TypeLocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class BienRequest {

  @NotBlank
  @Size(max = 120)
  private String nomUsuel;

  @NotBlank
  @Size(max = 200)
  private String adresseSimplifiee;

  @NotNull
  private StatutActiviteBien statutActivite;

  private TypeLocation typeLocation;

  private LocalDate dateMiseEnLocation;

  private RegimeVise regimeVise;

  @Size(max = 500)
  private String commentaire;

  public BienRequest() {}

  public String getNomUsuel() { return nomUsuel; }
  public void setNomUsuel(String nomUsuel) { this.nomUsuel = nomUsuel; }

  public String getAdresseSimplifiee() { return adresseSimplifiee; }
  public void setAdresseSimplifiee(String adresseSimplifiee) { this.adresseSimplifiee = adresseSimplifiee; }

  public StatutActiviteBien getStatutActivite() { return statutActivite; }
  public void setStatutActivite(StatutActiviteBien statutActivite) { this.statutActivite = statutActivite; }

  public TypeLocation getTypeLocation() { return typeLocation; }
  public void setTypeLocation(TypeLocation typeLocation) { this.typeLocation = typeLocation; }

  public LocalDate getDateMiseEnLocation() { return dateMiseEnLocation; }
  public void setDateMiseEnLocation(LocalDate dateMiseEnLocation) { this.dateMiseEnLocation = dateMiseEnLocation; }

  public RegimeVise getRegimeVise() { return regimeVise; }
  public void setRegimeVise(RegimeVise regimeVise) { this.regimeVise = regimeVise; }

  public String getCommentaire() { return commentaire; }
  public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}