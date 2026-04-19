package com.nemia.core.flux.dto;

import java.math.BigDecimal;
import java.util.List;

public class HomeSyntheseResponse {

  private Periode periode;
  private Metriques metriques;
  private Alertes alertes;
  private List<RepartitionDepense> repartitionDepenses;
  private RecurrenceDepenses recurrenceDepenses;
  private List<DerniereOperation> dernieresOperations;

  // ── Constructeur complet ──

  public HomeSyntheseResponse(
    Periode periode,
    Metriques metriques,
    Alertes alertes,
    List<RepartitionDepense> repartitionDepenses,
    RecurrenceDepenses recurrenceDepenses,
    List<DerniereOperation> dernieresOperations
  ) {
    this.periode = periode;
    this.metriques = metriques;
    this.alertes = alertes;
    this.repartitionDepenses = repartitionDepenses;
    this.recurrenceDepenses = recurrenceDepenses;
    this.dernieresOperations = dernieresOperations;
  }

  // ── Getters ──

  public Periode getPeriode() {
    return periode;
  }

  public Metriques getMetriques() {
    return metriques;
  }

  public Alertes getAlertes() {
    return alertes;
  }

  public List<RepartitionDepense> getRepartitionDepenses() {
    return repartitionDepenses;
  }

  public RecurrenceDepenses getRecurrenceDepenses() {
    return recurrenceDepenses;
  }

  public List<DerniereOperation> getDernieresOperations() {
    return dernieresOperations;
  }

  // ══════════════════════════════════════════
  // Classes internes statiques
  // ══════════════════════════════════════════

  public static class Periode {

    private String label;
    private String dateDebut;
    private String dateFin;
    private String mois;
    private Long exerciceId;
    private Long bienId;

    public Periode(String label, String dateDebut, String dateFin, String mois, Long exerciceId, Long bienId) {
      this.label = label;
      this.dateDebut = dateDebut;
      this.dateFin = dateFin;
      this.mois = mois;
      this.exerciceId = exerciceId;
      this.bienId = bienId;
    }

    public String getLabel() {
      return label;
    }

    public String getDateDebut() {
      return dateDebut;
    }

    public String getDateFin() {
      return dateFin;
    }

    public String getMois() {
      return mois;
    }

    public Long getExerciceId() {
      return exerciceId;
    }

    public Long getBienId() {
      return bienId;
    }
  }

  public static class Metriques {

    private BigDecimal totalRecettes;
    private BigDecimal totalDepenses;
    private BigDecimal solde;
    private long nombreOperations;

    public Metriques(BigDecimal totalRecettes, BigDecimal totalDepenses, BigDecimal solde, long nombreOperations) {
      this.totalRecettes = totalRecettes;
      this.totalDepenses = totalDepenses;
      this.solde = solde;
      this.nombreOperations = nombreOperations;
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

    public long getNombreOperations() {
      return nombreOperations;
    }
  }

  public static class Alertes {

    private long fluxSansJustificatif;
    private BigDecimal montantSansJustificatif;
    private long fluxAArbitrer;
    private BigDecimal montantAArbitrer;
    private long fluxARevoir;

    public Alertes(
      long fluxSansJustificatif,
      BigDecimal montantSansJustificatif,
      long fluxAArbitrer,
      BigDecimal montantAArbitrer,
      long fluxARevoir
    ) {
      this.fluxSansJustificatif = fluxSansJustificatif;
      this.montantSansJustificatif = montantSansJustificatif;
      this.fluxAArbitrer = fluxAArbitrer;
      this.montantAArbitrer = montantAArbitrer;
      this.fluxARevoir = fluxARevoir;
    }

    public long getFluxSansJustificatif() {
      return fluxSansJustificatif;
    }

    public BigDecimal getMontantSansJustificatif() {
      return montantSansJustificatif;
    }

    public long getFluxAArbitrer() {
      return fluxAArbitrer;
    }

    public BigDecimal getMontantAArbitrer() {
      return montantAArbitrer;
    }

    public long getFluxARevoir() {
      return fluxARevoir;
    }
  }

  public static class RepartitionDepense {

    private String categorie;
    private String label;
    private BigDecimal montant;

    public RepartitionDepense(String categorie, String label, BigDecimal montant) {
      this.categorie = categorie;
      this.label = label;
      this.montant = montant;
    }

    public String getCategorie() {
      return categorie;
    }

    public String getLabel() {
      return label;
    }

    public BigDecimal getMontant() {
      return montant;
    }
  }

  public static class RecurrenceDepenses {

    private BigDecimal montantRecurrent;
    private BigDecimal montantPonctuel;

    public RecurrenceDepenses(BigDecimal montantRecurrent, BigDecimal montantPonctuel) {
      this.montantRecurrent = montantRecurrent;
      this.montantPonctuel = montantPonctuel;
    }

    public BigDecimal getMontantRecurrent() {
      return montantRecurrent;
    }

    public BigDecimal getMontantPonctuel() {
      return montantPonctuel;
    }
  }

  public static class DerniereOperation {

    private Long id;
    private String date;
    private String libelle;
    private String type;
    private BigDecimal montant;
    private String categorie;
    private String statutJustificatif;
    private String qualificationPressentie;
    private List<String> warnings;

    public DerniereOperation(
      Long id,
      String date,
      String libelle,
      String type,
      BigDecimal montant,
      String categorie,
      String statutJustificatif,
      String qualificationPressentie,
      List<String> warnings
    ) {
      this.id = id;
      this.date = date;
      this.libelle = libelle;
      this.type = type;
      this.montant = montant;
      this.categorie = categorie;
      this.statutJustificatif = statutJustificatif;
      this.qualificationPressentie = qualificationPressentie;
      this.warnings = warnings;
    }

    public Long getId() {
      return id;
    }

    public String getDate() {
      return date;
    }

    public String getLibelle() {
      return libelle;
    }

    public String getType() {
      return type;
    }

    public BigDecimal getMontant() {
      return montant;
    }

    public String getCategorie() {
      return categorie;
    }

    public String getStatutJustificatif() {
      return statutJustificatif;
    }

    public String getQualificationPressentie() {
      return qualificationPressentie;
    }

    public List<String> getWarnings() {
      return warnings;
    }
  }
}
