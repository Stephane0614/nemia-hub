package com.nemia.core.travaux.service;

import com.nemia.core.common.exception.TravauxNotFoundException;
import com.nemia.core.travaux.dto.TravauxRequest;
import com.nemia.core.travaux.dto.TravauxResponse;
import com.nemia.core.travaux.model.Travaux;
import com.nemia.core.travaux.repository.TravauxRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TravauxService {

  private static final Logger logger = LoggerFactory.getLogger(TravauxService.class);

  private final TravauxRepository travauxRepository;

  public TravauxService(TravauxRepository travauxRepository) {
    this.travauxRepository = travauxRepository;
  }

  public TravauxResponse create(TravauxRequest request) {
    logger.info("Création d'un travaux : {}", request.getLibelleTravaux());
    validateDates(request);
    Travaux travaux = new Travaux();
    mapRequestToEntity(request, travaux);
    Travaux saved = travauxRepository.save(travaux);
    logger.info("Travaux créé avec succès : id={}", saved.getId());
    return mapToResponse(saved);
  }

  public List<TravauxResponse> findAll(Long bienId) {
    logger.info("Récupération des travaux, bienId={}", bienId);
    List<Travaux> liste = (bienId != null) ? travauxRepository.findByBienId(bienId) : travauxRepository.findAll();
    logger.info("Nombre de travaux récupérés : {}", liste.size());
    return liste.stream().map(this::mapToResponse).toList();
  }

  public TravauxResponse findById(Long id) {
    logger.info("Recherche du travaux id={}", id);
    Travaux travaux = travauxRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Travaux introuvable id={}", id);
        return new TravauxNotFoundException(id);
      });
    logger.info("Travaux trouvé : id={}", id);
    return mapToResponse(travaux);
  }

  public TravauxResponse update(Long id, TravauxRequest request) {
    logger.info("Mise à jour du travaux id={}", id);
    Travaux travaux = travauxRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Travaux introuvable pour mise à jour id={}", id);
        return new TravauxNotFoundException(id);
      });
    validateDates(request);
    mapRequestToEntity(request, travaux);
    Travaux updated = travauxRepository.save(travaux);
    logger.info("Travaux mis à jour : id={}", id);
    return mapToResponse(updated);
  }

  public void delete(Long id) {
    logger.info("Suppression du travaux id={}", id);
    Travaux travaux = travauxRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Travaux introuvable pour suppression id={}", id);
        return new TravauxNotFoundException(id);
      });
    travauxRepository.delete(travaux);
    logger.info("Travaux supprimé : id={}", id);
  }

  private void validateDates(TravauxRequest request) {
    if (request.getDateDebut() != null && request.getDateFin() != null && request.getDateFin().isBefore(request.getDateDebut())) {
      throw new IllegalArgumentException("La date de fin des travaux ne peut pas être antérieure à la date de début.");
    }
  }

  private void mapRequestToEntity(TravauxRequest request, Travaux travaux) {
    travaux.setLibelleTravaux(normalizeRequiredText(request.getLibelleTravaux()));
    travaux.setBienId(request.getBienId());
    travaux.setDateDebut(request.getDateDebut());
    travaux.setDateFin(request.getDateFin());
    travaux.setMontantTotal(request.getMontantTotal());
    travaux.setFinalitePressentie(request.getFinalitePressentie());
    travaux.setQualificationPressentie(request.getQualificationPressentie());
    travaux.setStatutTravaux(request.getStatutTravaux());
    travaux.setCommentaire(normalizeOptionalText(request.getCommentaire()));
  }

  private TravauxResponse mapToResponse(Travaux travaux) {
    TravauxResponse response = new TravauxResponse();
    response.setId(travaux.getId());
    response.setLibelleTravaux(travaux.getLibelleTravaux());
    response.setBienId(travaux.getBienId());
    response.setDateDebut(travaux.getDateDebut());
    response.setDateFin(travaux.getDateFin());
    response.setMontantTotal(travaux.getMontantTotal());
    response.setFinalitePressentie(travaux.getFinalitePressentie());
    response.setQualificationPressentie(travaux.getQualificationPressentie());
    response.setStatutTravaux(travaux.getStatutTravaux());
    response.setCommentaire(travaux.getCommentaire());
    response.setCreatedAt(travaux.getCreatedAt());
    response.setUpdatedAt(travaux.getUpdatedAt());
    return response;
  }

  private String normalizeRequiredText(String value) {
    return value.trim();
  }

  private String normalizeOptionalText(String value) {
    if (value == null) return null;
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
