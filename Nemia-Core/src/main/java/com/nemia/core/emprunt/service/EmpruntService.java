package com.nemia.core.emprunt.service;

import com.nemia.core.common.exception.EmpruntNotFoundException;
import com.nemia.core.emprunt.dto.EmpruntRequest;
import com.nemia.core.emprunt.dto.EmpruntResponse;
import com.nemia.core.emprunt.model.Emprunt;
import com.nemia.core.emprunt.repository.EmpruntRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmpruntService {

  private static final Logger logger = LoggerFactory.getLogger(EmpruntService.class);

  private final EmpruntRepository empruntRepository;

  public EmpruntService(EmpruntRepository empruntRepository) {
    this.empruntRepository = empruntRepository;
  }

  public EmpruntResponse create(EmpruntRequest request) {
    logger.info("Création d'un emprunt : {}", request.getReferencePret());
    validateDates(request);
    Emprunt emprunt = new Emprunt();
    mapRequestToEntity(request, emprunt);
    Emprunt saved = empruntRepository.save(emprunt);
    logger.info("Emprunt créé avec succès : id={}", saved.getId());
    return mapToResponse(saved);
  }

  public List<EmpruntResponse> findAll(Long bienId) {
    logger.info("Récupération des emprunts, bienId={}", bienId);
    List<Emprunt> liste = (bienId != null) ? empruntRepository.findByBienId(bienId) : empruntRepository.findAll();
    logger.info("Nombre d'emprunts récupérés : {}", liste.size());
    return liste.stream().map(this::mapToResponse).toList();
  }

  public EmpruntResponse findById(Long id) {
    logger.info("Recherche de l'emprunt id={}", id);
    Emprunt emprunt = empruntRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Emprunt introuvable id={}", id);
        return new EmpruntNotFoundException(id);
      });
    logger.info("Emprunt trouvé : id={}", id);
    return mapToResponse(emprunt);
  }

  public EmpruntResponse update(Long id, EmpruntRequest request) {
    logger.info("Mise à jour de l'emprunt id={}", id);
    Emprunt emprunt = empruntRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Emprunt introuvable pour mise à jour id={}", id);
        return new EmpruntNotFoundException(id);
      });
    validateDates(request);
    mapRequestToEntity(request, emprunt);
    Emprunt updated = empruntRepository.save(emprunt);
    logger.info("Emprunt mis à jour : id={}", id);
    return mapToResponse(updated);
  }

  public void delete(Long id) {
    logger.info("Suppression de l'emprunt id={}", id);
    Emprunt emprunt = empruntRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Emprunt introuvable pour suppression id={}", id);
        return new EmpruntNotFoundException(id);
      });
    empruntRepository.delete(emprunt);
    logger.info("Emprunt supprimé : id={}", id);
  }

  private void validateDates(EmpruntRequest request) {
    if (
      request.getDatePremiereEcheance() != null &&
      request.getDateDerniereEcheance() != null &&
      request.getDateDerniereEcheance().isBefore(request.getDatePremiereEcheance())
    ) {
      throw new IllegalArgumentException("La date de dernière échéance ne peut pas être antérieure à la date de première échéance.");
    }
  }

  private void mapRequestToEntity(EmpruntRequest request, Emprunt emprunt) {
    emprunt.setReferencePret(normalizeRequiredText(request.getReferencePret()));
    emprunt.setBienId(request.getBienId());
    emprunt.setOrganismePreteur(normalizeOptionalText(request.getOrganismePreteur()));
    emprunt.setMensualiteTotale(request.getMensualiteTotale());
    emprunt.setDatePremiereEcheance(request.getDatePremiereEcheance());
    emprunt.setDateDerniereEcheance(request.getDateDerniereEcheance());
    emprunt.setStatutEmprunt(request.getStatutEmprunt());
    emprunt.setCommentaire(normalizeOptionalText(request.getCommentaire()));
  }

  private EmpruntResponse mapToResponse(Emprunt emprunt) {
    EmpruntResponse response = new EmpruntResponse();
    response.setId(emprunt.getId());
    response.setReferencePret(emprunt.getReferencePret());
    response.setBienId(emprunt.getBienId());
    response.setOrganismePreteur(emprunt.getOrganismePreteur());
    response.setMensualiteTotale(emprunt.getMensualiteTotale());
    response.setDatePremiereEcheance(emprunt.getDatePremiereEcheance());
    response.setDateDerniereEcheance(emprunt.getDateDerniereEcheance());
    response.setStatutEmprunt(emprunt.getStatutEmprunt());
    response.setCommentaire(emprunt.getCommentaire());
    response.setCreatedAt(emprunt.getCreatedAt());
    response.setUpdatedAt(emprunt.getUpdatedAt());
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
