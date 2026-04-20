package com.nemia.core.flux.service;

import com.nemia.core.exercice.model.Exercice;
import com.nemia.core.exercice.repository.ExerciceRepository;
import com.nemia.core.flux.dto.HomeSyntheseResponse;
import com.nemia.core.flux.model.Flux;
import com.nemia.core.flux.repository.FluxRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class HomeSyntheseService {

  private final FluxRepository fluxRepository;
  private final FluxValidationService fluxValidationService;
  private final ExerciceRepository exerciceRepository;

  public HomeSyntheseService(
    FluxRepository fluxRepository,
    FluxValidationService fluxValidationService,
    ExerciceRepository exerciceRepository
  ) {
    this.fluxRepository = fluxRepository;
    this.fluxValidationService = fluxValidationService;
    this.exerciceRepository = exerciceRepository;
  }

  public HomeSyntheseResponse getSynthese(String mois, Long bienId, Long exerciceId) {
    LocalDate dateDebut;
    LocalDate dateFin;
    HomeSyntheseResponse.Periode periode;

    if (exerciceId != null) {
      Optional<Exercice> exerciceOpt = exerciceRepository.findById(exerciceId);
      if (exerciceOpt.isPresent()) {
        Exercice exercice = exerciceOpt.get();
        dateDebut = exercice.getDateDebut();
        dateFin = exercice.getDateFin();
        periode = new HomeSyntheseResponse.Periode(
          exercice.getLibelleExercice(),
          dateDebut.toString(),
          dateFin.toString(),
          null,
          exerciceId,
          bienId
        );
      } else {
        YearMonth yearMonth = parseMois(mois);
        dateDebut = yearMonth.atDay(1);
        dateFin = yearMonth.atEndOfMonth();
        periode = buildPeriodeMois(yearMonth, dateDebut, dateFin, null, bienId);
      }
    } else {
      YearMonth yearMonth = parseMois(mois);
      dateDebut = yearMonth.atDay(1);
      dateFin = yearMonth.atEndOfMonth();
      periode = buildPeriodeMois(yearMonth, dateDebut, dateFin, null, bienId);
    }

    return new HomeSyntheseResponse(
      periode,
      buildMetriques(dateDebut, dateFin, bienId),
      buildAlertes(dateDebut, dateFin, bienId),
      buildRepartitionDepenses(dateDebut, dateFin, bienId),
      buildRecurrenceDepenses(dateDebut, dateFin, bienId),
      buildDernieresOperations(dateDebut, dateFin, bienId)
    );
  }

  private HomeSyntheseResponse.Periode buildPeriodeMois(
    YearMonth yearMonth,
    LocalDate dateDebut,
    LocalDate dateFin,
    Long exerciceId,
    Long bienId
  ) {
    String label = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH));
    String labelFormate = Character.toUpperCase(label.charAt(0)) + label.substring(1);
    String moisCode = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    return new HomeSyntheseResponse.Periode(labelFormate, dateDebut.toString(), dateFin.toString(), moisCode, exerciceId, bienId);
  }

  private HomeSyntheseResponse.Metriques buildMetriques(LocalDate debut, LocalDate fin, Long bienId) {
    BigDecimal totalRecettes = fluxRepository.sumRecettes(debut, fin, bienId);
    BigDecimal totalDepenses = fluxRepository.sumDepenses(debut, fin, bienId);
    BigDecimal solde = totalRecettes.subtract(totalDepenses);
    long nombreOperations = fluxRepository.countOperations(debut, fin, bienId);
    return new HomeSyntheseResponse.Metriques(totalRecettes, totalDepenses, solde, nombreOperations);
  }

  private HomeSyntheseResponse.Alertes buildAlertes(LocalDate debut, LocalDate fin, Long bienId) {
    long fluxSansJustificatif = fluxRepository.countSansJustificatif(debut, fin, bienId);
    BigDecimal montantSansJustificatif = fluxRepository.sumMontantSansJustificatif(debut, fin, bienId);
    long fluxAArbitrer = fluxRepository.countAArbitrer(debut, fin, bienId);
    BigDecimal montantAArbitrer = fluxRepository.sumMontantAArbitrer(debut, fin, bienId);
    long fluxARevoir = fluxRepository.countARevoir(debut, fin, bienId);
    return new HomeSyntheseResponse.Alertes(fluxSansJustificatif, montantSansJustificatif, fluxAArbitrer, montantAArbitrer, fluxARevoir);
  }

  private List<HomeSyntheseResponse.RepartitionDepense> buildRepartitionDepenses(LocalDate debut, LocalDate fin, Long bienId) {
    List<Object[]> rows = fluxRepository.sumDepensesParCategorie(debut, fin, bienId);
    return rows
      .stream()
      .map(row -> {
        String categorie = row[0].toString();
        BigDecimal montant = (BigDecimal) row[1];
        String label = formatCategorie(categorie);
        return new HomeSyntheseResponse.RepartitionDepense(categorie, label, montant);
      })
      .toList();
  }

  private HomeSyntheseResponse.RecurrenceDepenses buildRecurrenceDepenses(LocalDate debut, LocalDate fin, Long bienId) {
    BigDecimal montantRecurrent = fluxRepository.sumDepensesRecurrentes(debut, fin, bienId);
    BigDecimal montantPonctuel = fluxRepository.sumDepensesPonctuelles(debut, fin, bienId);
    return new HomeSyntheseResponse.RecurrenceDepenses(montantRecurrent, montantPonctuel);
  }

  private List<HomeSyntheseResponse.DerniereOperation> buildDernieresOperations(LocalDate debut, LocalDate fin, Long bienId) {
    List<Flux> fluxes = fluxRepository.findDernieresOperations(debut, fin, bienId, PageRequest.of(0, 5));
    return fluxes
      .stream()
      .map(flux -> {
        List<String> warnings = fluxValidationService.computeWarnings(
          flux.getType(),
          flux.getCategorie(),
          flux.getQualificationPressentie(),
          flux.getStatutJustificatif(),
          flux.getOccurrence(),
          flux.getStatutTraitement()
        );
        return new HomeSyntheseResponse.DerniereOperation(
          flux.getId(),
          flux.getDate().toString(),
          flux.getLibelle(),
          flux.getType().name(),
          flux.getMontant(),
          flux.getCategorie().name(),
          flux.getStatutJustificatif() != null ? flux.getStatutJustificatif().name() : null,
          flux.getQualificationPressentie() != null ? flux.getQualificationPressentie().name() : null,
          warnings
        );
      })
      .toList();
  }

  private YearMonth parseMois(String mois) {
    if (mois == null || mois.isBlank()) {
      return YearMonth.now();
    }
    try {
      return YearMonth.parse(mois, DateTimeFormatter.ofPattern("yyyy-MM"));
    } catch (DateTimeParseException e) {
      throw new IllegalArgumentException("Format de mois invalide : '" + mois + "'. Format attendu : yyyy-MM");
    }
  }

  private String formatCategorie(String categorie) {
    return categorie.replace("_", " ").toLowerCase();
  }
}
