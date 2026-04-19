package com.nemia.core.justificatif.service;

import com.nemia.core.common.exception.JustificatifNotFoundException;
import com.nemia.core.justificatif.dto.JustificatifRequest;
import com.nemia.core.justificatif.dto.JustificatifResponse;
import com.nemia.core.justificatif.model.Justificatif;
import com.nemia.core.justificatif.repository.JustificatifRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JustificatifService {

  private static final Logger logger = LoggerFactory.getLogger(JustificatifService.class);

  private final JustificatifRepository justificatifRepository;

  public JustificatifService(JustificatifRepository justificatifRepository) {
    this.justificatifRepository = justificatifRepository;
  }

  public JustificatifResponse create(JustificatifRequest request) {
    logger.info("Création d'un justificatif : type={}", request.getTypePiece());
    Justificatif justificatif = new Justificatif();
    mapRequestToEntity(request, justificatif);
    Justificatif saved = justificatifRepository.save(justificatif);
    logger.info("Justificatif créé avec succès : id={}", saved.getId());
    return mapToResponse(saved);
  }

  public List<JustificatifResponse> findAll() {
    logger.info("Récupération de tous les justificatifs");
    List<JustificatifResponse> list = justificatifRepository.findAll()
      .stream()
      .map(this::mapToResponse)
      .toList();
    logger.info("Nombre de justificatifs récupérés : {}", list.size());
    return list;
  }

  public JustificatifResponse findById(Long id) {
    logger.info("Recherche du justificatif id={}", id);
    Justificatif justificatif = justificatifRepository.findById(id)
      .orElseThrow(() -> {
        logger.warn("Justificatif introuvable id={}", id);
        return new JustificatifNotFoundException(id);
      });
    logger.info("Justificatif trouvé : id={}", id);
    return mapToResponse(justificatif);
  }

  public JustificatifResponse update(Long id, JustificatifRequest request) {
    logger.info("Mise à jour du justificatif id={}", id);
    Justificatif justificatif = justificatifRepository.findById(id)
      .orElseThrow(() -> {
        logger.warn("Justificatif introuvable pour mise à jour id={}", id);
        return new JustificatifNotFoundException(id);
      });
    mapRequestToEntity(request, justificatif);
    Justificatif updated = justificatifRepository.save(justificatif);
    logger.info("Justificatif mis à jour : id={}", id);
    return mapToResponse(updated);
  }

  public void delete(Long id) {
    logger.info("Suppression du justificatif id={}", id);
    Justificatif justificatif = justificatifRepository.findById(id)
      .orElseThrow(() -> {
        logger.warn("Justificatif introuvable pour suppression id={}", id);
        return new JustificatifNotFoundException(id);
      });
    justificatifRepository.delete(justificatif);
    logger.info("Justificatif supprimé : id={}", id);
  }

  private void mapRequestToEntity(JustificatifRequest request, Justificatif justificatif) {
    justificatif.setTypePiece(request.getTypePiece());
    justificatif.setStatutDocumentaire(request.getStatutDocumentaire());
    justificatif.setDatePiece(request.getDatePiece());
    justificatif.setReferencePiece(normalizeOptionalText(request.getReferencePiece()));
    justificatif.setEmetteur(normalizeOptionalText(request.getEmetteur()));
    justificatif.setCommentaire(normalizeOptionalText(request.getCommentaire()));
    justificatif.setFichierAssocie(normalizeOptionalText(request.getFichierAssocie()));
  }

  private JustificatifResponse mapToResponse(Justificatif justificatif) {
    JustificatifResponse response = new JustificatifResponse();
    response.setId(justificatif.getId());
    response.setTypePiece(justificatif.getTypePiece());
    response.setStatutDocumentaire(justificatif.getStatutDocumentaire());
    response.setDatePiece(justificatif.getDatePiece());
    response.setReferencePiece(justificatif.getReferencePiece());
    response.setEmetteur(justificatif.getEmetteur());
    response.setCommentaire(justificatif.getCommentaire());
    response.setFichierAssocie(justificatif.getFichierAssocie());
    response.setCreatedAt(justificatif.getCreatedAt());
    response.setUpdatedAt(justificatif.getUpdatedAt());
    return response;
  }

  private String normalizeOptionalText(String value) {
    if (value == null) return null;
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}