package com.nemia.core.exercice.dto;

import com.nemia.core.exercice.model.NiveauCompletude;
import com.nemia.core.exercice.model.StatutExercice;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class ExerciceSyntheseResponse {

  private IdentiteExercice identite;
  private BlocFinancier financier;
  private BlocCompletude completude;
  private BlocQualification qualification;

  public ExerciceSyntheseResponse(
    IdentiteExercice identite,
    BlocFinancier financier,
    BlocCompletude completude,
    BlocQualification qualification
  ) {
    this.identite = identite;
    this.financier = financier;
    this.completude = completude;
    this.qualification = qualification;
  }

  // ── Getters ──

  public IdentiteExercice getIdentite() {
    return identite;
  }

  public BlocFinancier getFinancier() {
    return financier;
  }

  public BlocCompletude getCompletude() {
    return completude;
  }

  public BlocQualification getQualification() {
    return qualification;
  }

  // -------------------------------------------------------------------------
  // Blocs internes
  // -------------------------------------------------------------------------

  public static class IdentiteExercice {

    private Long id;
    private String libelle;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private StatutExercice statut;
    private NiveauCompletude niveauCompletude;

    public IdentiteExercice(
      Long id,
      String libelle,
      LocalDate dateDebut,
      LocalDate dateFin,
      StatutExercice statut,
      NiveauCompletude niveauCompletude
    ) {
      this.id = id;
      this.libelle = libelle;
      this.dateDebut = dateDebut;
      this.dateFin = dateFin;
      this.statut = statut;
      this.niveauCompletude = niveauCompletude;
    }

    public Long getId() {
      return id;
    }

    public String getLibelle() {
      return libelle;
    }

    public LocalDate getDateDebut() {
      return dateDebut;
    }

    public LocalDate getDateFin() {
      return dateFin;
    }

    public StatutExercice getStatut() {
      return statut;
    }

    public NiveauCompletude getNiveauCompletude() {
      return niveauCompletude;
    }
  }

  public static class BlocFinancier {

    private BigDecimal totalRecettes;
    private BigDecimal totalDepenses;
    private BigDecimal solde;
    private long nombreFluxTotal;
    private List<RepartitionCategorie> repartitionRecettes;
    private List<RepartitionCategorie> repartitionDepenses;
    private List<RepartitionTypeFlux> repartitionTypeFlux;

    public BlocFinancier(
      BigDecimal totalRecettes,
      BigDecimal totalDepenses,
      long nombreFluxTotal,
      List<RepartitionCategorie> repartitionRecettes,
      List<RepartitionCategorie> repartitionDepenses,
      List<RepartitionTypeFlux> repartitionTypeFlux
    ) {
      this.totalRecettes = totalRecettes;
      this.totalDepenses = totalDepenses;
      this.solde = totalRecettes.subtract(totalDepenses);
      this.nombreFluxTotal = nombreFluxTotal;
      this.repartitionRecettes = repartitionRecettes;
      this.repartitionDepenses = repartitionDepenses;
      this.repartitionTypeFlux = repartitionTypeFlux;
    }

    public BigDecimal getTotalRecettes() {
      return totalRecettes;
    }

    public BigDecimal getTotalDepenses() {
      return totalDepenses;
    }

    public BigDecimal getSolde() {
      return solde;
    }

    public long getNombreFluxTotal() {
      return nombreFluxTotal;
    }

    public List<RepartitionCategorie> getRepartitionRecettes() {
      return repartitionRecettes;
    }

    public List<RepartitionCategorie> getRepartitionDepenses() {
      return repartitionDepenses;
    }

    public List<RepartitionTypeFlux> getRepartitionTypeFlux() {
      return repartitionTypeFlux;
    }
  }

  public static class RepartitionCategorie {

    private String code;
    private String label;
    private BigDecimal montant;

    public RepartitionCategorie(String code, String label, BigDecimal montant) {
      this.code = code;
      this.label = label;
      this.montant = montant;
    }

    public String getCode() {
      return code;
    }

    public String getLabel() {
      return label;
    }

    public BigDecimal getMontant() {
      return montant;
    }
  }

  public static class RepartitionTypeFlux {

    private String type;
    private long nombre;
    private BigDecimal montant;

    public RepartitionTypeFlux(String type, long nombre, BigDecimal montant) {
      this.type = type;
      this.nombre = nombre;
      this.montant = montant;
    }

    public String getType() {
      return type;
    }

    public long getNombre() {
      return nombre;
    }

    public BigDecimal getMontant() {
      return montant;
    }
  }

  public static class BlocCompletude {

    private long nombreFluxSansJustificatif;
    private BigDecimal montantSansJustificatif;
    private long nombreFluxAArbitrer;
    private BigDecimal montantAArbitrer;
    private long nombreFluxARevoir;
    private BigDecimal montantARevoir;
    private BigDecimal tauxJustificationGlobal;

    public BlocCompletude(
      long nombreFluxSansJustificatif,
      BigDecimal montantSansJustificatif,
      long nombreFluxAArbitrer,
      BigDecimal montantAArbitrer,
      long nombreFluxARevoir,
      BigDecimal montantARevoir,
      long nombreFluxDepensesTotal,
      long nombreFluxDepensesFournis
    ) {
      this.nombreFluxSansJustificatif = nombreFluxSansJustificatif;
      this.montantSansJustificatif = montantSansJustificatif;
      this.nombreFluxAArbitrer = nombreFluxAArbitrer;
      this.montantAArbitrer = montantAArbitrer;
      this.nombreFluxARevoir = nombreFluxARevoir;
      this.montantARevoir = montantARevoir;
      this.tauxJustificationGlobal = calculerTaux(nombreFluxDepensesTotal, nombreFluxDepensesFournis);
    }

    private BigDecimal calculerTaux(long total, long fournis) {
      if (total == 0) return BigDecimal.ZERO;
      return BigDecimal.valueOf((fournis * 100.0) / total).setScale(1, RoundingMode.HALF_UP);
    }

    public long getNombreFluxSansJustificatif() {
      return nombreFluxSansJustificatif;
    }

    public BigDecimal getMontantSansJustificatif() {
      return montantSansJustificatif;
    }

    public long getNombreFluxAArbitrer() {
      return nombreFluxAArbitrer;
    }

    public BigDecimal getMontantAArbitrer() {
      return montantAArbitrer;
    }

    public long getNombreFluxARevoir() {
      return nombreFluxARevoir;
    }

    public BigDecimal getMontantARevoir() {
      return montantARevoir;
    }

    public BigDecimal getTauxJustificationGlobal() {
      return tauxJustificationGlobal;
    }
  }

  public static class BlocQualification {

    private List<RepartitionQualification> repartition;

    public BlocQualification(List<RepartitionQualification> repartition) {
      this.repartition = repartition;
    }

    public List<RepartitionQualification> getRepartition() {
      return repartition;
    }
  }

  public static class RepartitionQualification {

    private String qualification;
    private long nombre;
    private BigDecimal montant;

    public RepartitionQualification(String qualification, long nombre, BigDecimal montant) {
      this.qualification = qualification;
      this.nombre = nombre;
      this.montant = montant;
    }

    public String getQualification() {
      return qualification;
    }

    public long getNombre() {
      return nombre;
    }

    public BigDecimal getMontant() {
      return montant;
    }
  }
}
