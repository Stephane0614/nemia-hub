package com.nemia.core.exercice.service;

import com.nemia.core.common.exception.ExerciceAlreadyExistsException;
import com.nemia.core.common.exception.ExerciceNotFoundException;
import com.nemia.core.exercice.dto.ExerciceRequest;
import com.nemia.core.exercice.dto.ExerciceResponse;
import com.nemia.core.exercice.dto.ExerciceSyntheseResponse;
import com.nemia.core.exercice.model.Exercice;
import com.nemia.core.exercice.model.NiveauCompletude;
import com.nemia.core.exercice.model.StatutExercice;
import com.nemia.core.exercice.repository.ExerciceRepository;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.repository.FluxRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExerciceService {

  private static final Logger logger = LoggerFactory.getLogger(ExerciceService.class);

  private final ExerciceRepository exerciceRepository;
  private final FluxRepository fluxRepository;

  public ExerciceService(ExerciceRepository exerciceRepository, FluxRepository fluxRepository) {
    this.exerciceRepository = exerciceRepository;
    this.fluxRepository = fluxRepository;
  }

  public ExerciceResponse create(ExerciceRequest request) {
    logger.info("Création d'un exercice : {}", request.getLibelleExercice());

    String libelle = request.getLibelleExercice().trim();

    if (exerciceRepository.existsByLibelleExercice(libelle)) {
      logger.warn("Doublon détecté à la création : {}", libelle);
      throw new ExerciceAlreadyExistsException(libelle);
    }

    validateDates(request);

    Exercice exercice = new Exercice();
    mapRequestToEntity(request, exercice);
    Exercice saved = exerciceRepository.save(exercice);
    logger.info("Exercice créé avec succès : id={}", saved.getId());

    ExerciceResponse response = mapToResponse(saved);
    response.setWarnings(collectWarnings(request.getStatutExercice(), request.getNiveauCompletude()));
    return response;
  }

  public List<ExerciceResponse> findAll() {
    logger.info("Récupération de tous les exercices");
    List<ExerciceResponse> exercices = exerciceRepository.findAll().stream().map(this::mapToResponse).toList();
    logger.info("Nombre d'exercices récupérés : {}", exercices.size());
    return exercices;
  }

  public ExerciceResponse findById(Long id) {
    logger.info("Recherche de l'exercice id={}", id);
    Exercice exercice = exerciceRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Exercice introuvable id={}", id);
        return new ExerciceNotFoundException(id);
      });
    logger.info("Exercice trouvé : id={}", id);
    return mapToResponse(exercice);
  }

  public ExerciceResponse update(Long id, ExerciceRequest request) {
    logger.info("Mise à jour de l'exercice id={}", id);

    Exercice exercice = exerciceRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Exercice introuvable pour mise à jour id={}", id);
        return new ExerciceNotFoundException(id);
      });

    String libelle = request.getLibelleExercice().trim();

    if (exerciceRepository.existsByLibelleExerciceAndIdNot(libelle, id)) {
      logger.warn("Doublon détecté à la modification : {}", libelle);
      throw new ExerciceAlreadyExistsException(libelle);
    }

    validateDates(request);

    mapRequestToEntity(request, exercice);
    Exercice updated = exerciceRepository.save(exercice);
    logger.info("Exercice mis à jour : id={}", id);

    ExerciceResponse response = mapToResponse(updated);
    response.setWarnings(collectWarnings(request.getStatutExercice(), request.getNiveauCompletude()));
    return response;
  }

  public void delete(Long id) {
    logger.info("Suppression de l'exercice id={}", id);
    Exercice exercice = exerciceRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Exercice introuvable pour suppression id={}", id);
        return new ExerciceNotFoundException(id);
      });
    exerciceRepository.delete(exercice);
    logger.info("Exercice supprimé : id={}", id);
  }

  private void validateDates(ExerciceRequest request) {
    if (!request.getDateFin().isAfter(request.getDateDebut())) {
      throw new IllegalArgumentException("La date de fin doit être strictement postérieure à la date de début.");
    }
  }

  private List<String> collectWarnings(StatutExercice statut, NiveauCompletude niveau) {
    List<String> warnings = new ArrayList<>();

    if (statut == null || niveau == null) return warnings;

    if (statut == StatutExercice.CLOTURE && niveau != NiveauCompletude.COMPLET) {
      warnings.add("Un exercice clôturé devrait avoir un niveau de complétude Complet.");
    }

    if (statut == StatutExercice.EN_PREPARATION_DE_CLOTURE && niveau == NiveauCompletude.FAIBLE) {
      warnings.add("Un exercice en préparation de clôture devrait avoir un niveau de complétude au moins Moyen.");
    }

    return warnings;
  }

  private void mapRequestToEntity(ExerciceRequest request, Exercice exercice) {
    exercice.setLibelleExercice(request.getLibelleExercice().trim());
    exercice.setDateDebut(request.getDateDebut());
    exercice.setDateFin(request.getDateFin());
    exercice.setStatutExercice(request.getStatutExercice());
    exercice.setNiveauCompletude(request.getNiveauCompletude());
    exercice.setCommentaire(normalizeOptionalText(request.getCommentaire()));
  }

  private ExerciceResponse mapToResponse(Exercice exercice) {
    ExerciceResponse response = new ExerciceResponse();
    response.setId(exercice.getId());
    response.setLibelleExercice(exercice.getLibelleExercice());
    response.setDateDebut(exercice.getDateDebut());
    response.setDateFin(exercice.getDateFin());
    response.setStatutExercice(exercice.getStatutExercice());
    response.setNiveauCompletude(exercice.getNiveauCompletude());
    response.setCommentaire(exercice.getCommentaire());
    response.setCreatedAt(exercice.getCreatedAt());
    response.setUpdatedAt(exercice.getUpdatedAt());
    return response;
  }

  private String normalizeOptionalText(String value) {
    if (value == null) return null;
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  public ExerciceSyntheseResponse getSynthese(Long id) {
    Exercice exercice = exerciceRepository.findById(id).orElseThrow(() -> new ExerciceNotFoundException(id));

    // -- Identité
    ExerciceSyntheseResponse.IdentiteExercice identite = new ExerciceSyntheseResponse.IdentiteExercice(
      exercice.getId(),
      exercice.getLibelleExercice(),
      exercice.getDateDebut(),
      exercice.getDateFin(),
      exercice.getStatutExercice(),
      exercice.getNiveauCompletude()
    );

    // -- Financier
    BigDecimal totalRecettes = fluxRepository.sumRecettesParExercice(id);
    BigDecimal totalDepenses = fluxRepository.sumDepensesParExercice(id);
    long nombreFluxTotal = fluxRepository.countFluxParExercice(id);

    List<ExerciceSyntheseResponse.RepartitionCategorie> repartitionRecettes = fluxRepository
      .sumRecettesParCategorieEtExercice(id)
      .stream()
      .map(row ->
        new ExerciceSyntheseResponse.RepartitionCategorie(row[0].toString(), labelCategorie(row[0].toString()), (BigDecimal) row[1])
      )
      .toList();

    List<ExerciceSyntheseResponse.RepartitionCategorie> repartitionDepenses = fluxRepository
      .sumDepensesParCategorieEtExercice(id)
      .stream()
      .map(row ->
        new ExerciceSyntheseResponse.RepartitionCategorie(row[0].toString(), labelCategorie(row[0].toString()), (BigDecimal) row[1])
      )
      .toList();

    List<ExerciceSyntheseResponse.RepartitionTypeFlux> repartitionTypeFlux = fluxRepository
      .repartitionParTypeEtExercice(id)
      .stream()
      .map(row -> new ExerciceSyntheseResponse.RepartitionTypeFlux(row[0].toString(), ((Number) row[1]).longValue(), (BigDecimal) row[2]))
      .toList();

    ExerciceSyntheseResponse.BlocFinancier financier = new ExerciceSyntheseResponse.BlocFinancier(
      totalRecettes,
      totalDepenses,
      nombreFluxTotal,
      repartitionRecettes,
      repartitionDepenses,
      repartitionTypeFlux
    );

    // -- Complétude
    long nbSansJustificatif = fluxRepository.countSansJustificatifParExercice(id);
    BigDecimal montantSansJustificatif = fluxRepository.sumMontantSansJustificatifParExercice(id);
    long nbAArbitrer = fluxRepository.countAArbitrerParExercice(id);
    BigDecimal montantAArbitrer = fluxRepository.sumMontantAArbitrerParExercice(id);
    long nbARevoir = fluxRepository.countARevoirParExercice(id);
    BigDecimal montantARevoir = fluxRepository.sumMontantARevoirParExercice(id);
    long nbDepensesTotal = fluxRepository.countDepensesParExercice(id);
    long nbDepensesFournis = fluxRepository.countDepensesFournisParExercice(id);

    ExerciceSyntheseResponse.BlocCompletude completude = new ExerciceSyntheseResponse.BlocCompletude(
      nbSansJustificatif,
      montantSansJustificatif,
      nbAArbitrer,
      montantAArbitrer,
      nbARevoir,
      montantARevoir,
      nbDepensesTotal,
      nbDepensesFournis
    );

    // -- Qualification
    // On s'assure que toutes les valeurs de l'enum sont présentes, même à zéro
    Map<String, long[]> qualMap = fluxRepository
      .repartitionQualificationParExercice(id)
      .stream()
      .collect(
        Collectors.toMap(row -> row[0].toString(), row -> new long[] { ((Number) row[1]).longValue(), ((Number) row[2]).longValue() })
      );

    List<ExerciceSyntheseResponse.RepartitionQualification> repartitionQual = Arrays.stream(
      com.nemia.core.flux.model.QualificationPressentie.values()
    )
      .map(q -> {
        long[] vals = qualMap.getOrDefault(q.name(), new long[] { 0L, 0L });
        return new ExerciceSyntheseResponse.RepartitionQualification(q.name(), vals[0], BigDecimal.valueOf(vals[1]));
      })
      .toList();

    ExerciceSyntheseResponse.BlocQualification qualification = new ExerciceSyntheseResponse.BlocQualification(repartitionQual);

    return new ExerciceSyntheseResponse(identite, financier, completude, qualification);
  }

  public ExerciceResponse getExerciceEnCours() {
    LocalDate today = LocalDate.now();

    // Priorité 1 : exercice OUVERT couvrant la date du jour
    List<Exercice> couvrants = exerciceRepository.findByStatutAndDateCovering(StatutExercice.OUVERT, today);
    if (!couvrants.isEmpty()) {
      return mapToResponse(couvrants.get(0));
    }

    // Priorité 2 : dernier exercice OUVERT en fallback
    List<Exercice> ouverts = exerciceRepository.findByStatutOrderByDateFinDesc(StatutExercice.OUVERT);
    if (!ouverts.isEmpty()) {
      return mapToResponse(ouverts.get(0));
    }

    // Aucun exercice OUVERT
    throw new ExerciceNotFoundException(0L);
  }

  private String labelCategorie(String code) {
    try {
      return FluxCategory.valueOf(code).getLabel();
    } catch (IllegalArgumentException e) {
      return code;
    }
  }
}
