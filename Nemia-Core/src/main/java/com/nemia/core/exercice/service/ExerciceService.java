package com.nemia.core.exercice.service;

import com.nemia.core.common.exception.ExerciceAlreadyExistsException;
import com.nemia.core.common.exception.ExerciceNotFoundException;
import com.nemia.core.exercice.dto.ExerciceRequest;
import com.nemia.core.exercice.dto.ExerciceResponse;
import com.nemia.core.exercice.model.Exercice;
import com.nemia.core.exercice.repository.ExerciceRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExerciceService {

  private static final Logger logger = LoggerFactory.getLogger(ExerciceService.class);

  private final ExerciceRepository exerciceRepository;

  public ExerciceService(ExerciceRepository exerciceRepository) {
    this.exerciceRepository = exerciceRepository;
  }

  public ExerciceResponse create(ExerciceRequest request) {
    logger.info("Création d'un exercice : {}", request.getLibelleExercice());

    String libelle = request.getLibelleExercice().trim();

    exerciceRepository
      .findByLibelleExercice(libelle)
      .ifPresent(existing -> {
        logger.warn("Doublon détecté à la création : {}", libelle);
        throw new ExerciceAlreadyExistsException(libelle);
      });

    validateDates(request);

    Exercice exercice = new Exercice();
    mapRequestToEntity(request, exercice);
    Exercice saved = exerciceRepository.save(exercice);
    logger.info("Exercice créé avec succès : id={}", saved.getId());
    return mapToResponse(saved);
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

    exerciceRepository
      .findByLibelleExercice(libelle)
      .ifPresent(existing -> {
        if (!existing.getId().equals(id)) {
          logger.warn("Doublon détecté à la modification : {}", libelle);
          throw new ExerciceAlreadyExistsException(libelle);
        }
      });

    validateDates(request);

    mapRequestToEntity(request, exercice);
    Exercice updated = exerciceRepository.save(exercice);
    logger.info("Exercice mis à jour : id={}", id);
    return mapToResponse(updated);
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
}
