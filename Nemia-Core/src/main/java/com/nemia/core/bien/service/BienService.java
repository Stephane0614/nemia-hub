package com.nemia.core.bien.service;

import com.nemia.core.bien.dto.BienRequest;
import com.nemia.core.bien.dto.BienResponse;
import com.nemia.core.bien.model.Bien;
import com.nemia.core.bien.repository.BienRepository;
import com.nemia.core.common.exception.BienAlreadyExistsException;
import com.nemia.core.common.exception.BienNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BienService {

  private static final Logger logger = LoggerFactory.getLogger(BienService.class);

  private final BienRepository bienRepository;

  public BienService(BienRepository bienRepository) {
    this.bienRepository = bienRepository;
  }

  public BienResponse create(BienRequest request) {
    logger.info("Création d'un bien : {}", request.getNomUsuel());

    String nomUsuel = request.getNomUsuel().trim();
    String adresse = request.getAdresseSimplifiee().trim();

    bienRepository
      .findByNomUsuelAndAdresseSimplifiee(nomUsuel, adresse)
      .ifPresent(existing -> {
        logger.warn("Doublon détecté à la création : {} — {}", nomUsuel, adresse);
        throw new BienAlreadyExistsException(nomUsuel, adresse);
      });

    Bien bien = new Bien();
    mapRequestToEntity(request, bien);
    Bien saved = bienRepository.save(bien);
    logger.info("Bien créé avec succès : id={}", saved.getId());
    return mapToResponse(saved);
  }

  public List<BienResponse> findAll() {
    logger.info("Récupération de tous les biens");
    List<BienResponse> biens = bienRepository.findAll().stream().map(this::mapToResponse).toList();
    logger.info("Nombre de biens récupérés : {}", biens.size());
    return biens;
  }

  public BienResponse findById(Long id) {
    logger.info("Recherche du bien id={}", id);
    Bien bien = bienRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Bien introuvable id={}", id);
        return new BienNotFoundException(id);
      });
    logger.info("Bien trouvé : id={}", id);
    return mapToResponse(bien);
  }

  public BienResponse update(Long id, BienRequest request) {
    logger.info("Mise à jour du bien id={}", id);

    Bien bien = bienRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Bien introuvable pour mise à jour id={}", id);
        return new BienNotFoundException(id);
      });

    String nomUsuel = request.getNomUsuel().trim();
    String adresse = request.getAdresseSimplifiee().trim();

    bienRepository
      .findByNomUsuelAndAdresseSimplifiee(nomUsuel, adresse)
      .ifPresent(existing -> {
        if (!existing.getId().equals(id)) {
          logger.warn("Doublon détecté à la modification : {} — {}", nomUsuel, adresse);
          throw new BienAlreadyExistsException(nomUsuel, adresse);
        }
      });

    mapRequestToEntity(request, bien);
    Bien updated = bienRepository.save(bien);
    logger.info("Bien mis à jour : id={}", id);
    return mapToResponse(updated);
  }

  public void delete(Long id) {
    logger.info("Suppression du bien id={}", id);
    Bien bien = bienRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Bien introuvable pour suppression id={}", id);
        return new BienNotFoundException(id);
      });
    bienRepository.delete(bien);
    logger.info("Bien supprimé : id={}", id);
  }

  private void mapRequestToEntity(BienRequest request, Bien bien) {
    bien.setNomUsuel(request.getNomUsuel().trim());
    bien.setAdresseSimplifiee(request.getAdresseSimplifiee().trim());
    bien.setStatutActivite(request.getStatutActivite());
    bien.setTypeLocation(request.getTypeLocation());
    bien.setDateMiseEnLocation(request.getDateMiseEnLocation());
    bien.setRegimeVise(request.getRegimeVise());
    bien.setCommentaire(normalizeOptionalText(request.getCommentaire()));
  }

  private BienResponse mapToResponse(Bien bien) {
    BienResponse response = new BienResponse();
    response.setId(bien.getId());
    response.setNomUsuel(bien.getNomUsuel());
    response.setAdresseSimplifiee(bien.getAdresseSimplifiee());
    response.setStatutActivite(bien.getStatutActivite());
    response.setTypeLocation(bien.getTypeLocation());
    response.setDateMiseEnLocation(bien.getDateMiseEnLocation());
    response.setRegimeVise(bien.getRegimeVise());
    response.setCommentaire(bien.getCommentaire());
    response.setCreatedAt(bien.getCreatedAt());
    response.setUpdatedAt(bien.getUpdatedAt());
    return response;
  }

  private String normalizeOptionalText(String value) {
    if (value == null) return null;
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
