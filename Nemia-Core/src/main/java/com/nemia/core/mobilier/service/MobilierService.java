package com.nemia.core.mobilier.service;

import com.nemia.core.common.exception.MobilierNotFoundException;
import com.nemia.core.justificatif.repository.JustificatifRepository;
import com.nemia.core.mobilier.dto.MobilierRequest;
import com.nemia.core.mobilier.dto.MobilierResponse;
import com.nemia.core.mobilier.model.Mobilier;
import com.nemia.core.mobilier.repository.MobilierRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MobilierService {

    private static final Logger logger = LoggerFactory.getLogger(MobilierService.class);

    private final MobilierRepository mobilierRepository;
    private final JustificatifRepository justificatifRepository;

    public MobilierService(
            MobilierRepository mobilierRepository,
            JustificatifRepository justificatifRepository) {
        this.mobilierRepository = mobilierRepository;
        this.justificatifRepository = justificatifRepository;
    }

    public MobilierResponse create(MobilierRequest request) {
        logger.info("Création d'un mobilier : {}", request.getDesignation());
        validateJustificatifId(request.getJustificatifId());
        Mobilier mobilier = new Mobilier();
        mapRequestToEntity(request, mobilier);
        Mobilier saved = mobilierRepository.save(mobilier);
        logger.info("Mobilier créé avec succès : id={}", saved.getId());
        return mapToResponse(saved);
    }

    public List<MobilierResponse> findAll(Long bienId) {
        logger.info("Récupération du mobilier, bienId={}", bienId);
        List<Mobilier> liste = (bienId != null)
            ? mobilierRepository.findByBienId(bienId)
            : mobilierRepository.findAll();
        logger.info("Nombre de mobilier récupérés : {}", liste.size());
        return liste.stream().map(this::mapToResponse).toList();
    }

    public MobilierResponse findById(Long id) {
        logger.info("Recherche du mobilier id={}", id);
        Mobilier mobilier = mobilierRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Mobilier introuvable id={}", id);
                return new MobilierNotFoundException(id);
            });
        logger.info("Mobilier trouvé : id={}", id);
        return mapToResponse(mobilier);
    }

    public MobilierResponse update(Long id, MobilierRequest request) {
        logger.info("Mise à jour du mobilier id={}", id);
        Mobilier mobilier = mobilierRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Mobilier introuvable pour mise à jour id={}", id);
                return new MobilierNotFoundException(id);
            });
        validateJustificatifId(request.getJustificatifId());
        mapRequestToEntity(request, mobilier);
        Mobilier updated = mobilierRepository.save(mobilier);
        logger.info("Mobilier mis à jour : id={}", id);
        return mapToResponse(updated);
    }

    public void delete(Long id) {
        logger.info("Suppression du mobilier id={}", id);
        Mobilier mobilier = mobilierRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Mobilier introuvable pour suppression id={}", id);
                return new MobilierNotFoundException(id);
            });
        mobilierRepository.delete(mobilier);
        logger.info("Mobilier supprimé : id={}", id);
    }

    private void validateJustificatifId(Long justificatifId) {
        if (justificatifId != null && !justificatifRepository.existsById(justificatifId)) {
            throw new IllegalArgumentException("Justificatif introuvable avec l'id : " + justificatifId);
        }
    }

    private void mapRequestToEntity(MobilierRequest request, Mobilier mobilier) {
        mobilier.setDesignation(normalizeRequiredText(request.getDesignation()));
        mobilier.setBienId(request.getBienId());
        mobilier.setDateAcquisition(request.getDateAcquisition());
        mobilier.setMontant(request.getMontant());
        mobilier.setQuantite(request.getQuantite() != null ? request.getQuantite() : 1);
        mobilier.setCategorieMobilier(request.getCategorieMobilier());
        mobilier.setEtatUsage(request.getEtatUsage());
        mobilier.setQualificationPressentie(request.getQualificationPressentie());
        mobilier.setStatutMobilier(request.getStatutMobilier());
        mobilier.setJustificatifId(request.getJustificatifId());
        mobilier.setCommentaire(normalizeOptionalText(request.getCommentaire()));
    }

    private MobilierResponse mapToResponse(Mobilier mobilier) {
        MobilierResponse response = new MobilierResponse();
        response.setId(mobilier.getId());
        response.setDesignation(mobilier.getDesignation());
        response.setBienId(mobilier.getBienId());
        response.setDateAcquisition(mobilier.getDateAcquisition());
        response.setMontant(mobilier.getMontant());
        response.setQuantite(mobilier.getQuantite());
        response.setCategorieMobilier(mobilier.getCategorieMobilier());
        response.setEtatUsage(mobilier.getEtatUsage());
        response.setQualificationPressentie(mobilier.getQualificationPressentie());
        response.setStatutMobilier(mobilier.getStatutMobilier());
        response.setJustificatifId(mobilier.getJustificatifId());
        response.setCommentaire(mobilier.getCommentaire());
        response.setCreatedAt(mobilier.getCreatedAt());
        response.setUpdatedAt(mobilier.getUpdatedAt());
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