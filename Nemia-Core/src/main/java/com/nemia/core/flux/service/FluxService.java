package com.nemia.core.flux.service;

import com.nemia.core.common.exception.FluxNotFoundException;
import com.nemia.core.flux.dto.CreateFluxRequest;
import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.dto.UpdateFluxRequest;
import com.nemia.core.flux.entity.Flux;
import com.nemia.core.flux.repository.FluxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FluxService {

    private static final Logger logger = LoggerFactory.getLogger(FluxService.class);

    private final FluxRepository fluxRepository;

    public FluxService(FluxRepository fluxRepository) {
        this.fluxRepository = fluxRepository;
    }

    public FluxResponse create(CreateFluxRequest request) {
        logger.info("Création d'un flux : type={}, libelle={}, montant={}",
                request.getType(),
                request.getLibelle(),
                request.getMontant());

        Flux flux = new Flux();
        mapCreateRequestToEntity(request, flux);

        Flux savedFlux = fluxRepository.save(flux);

        logger.info("Flux créé avec succès : id={}", savedFlux.getId());
        return mapToResponse(savedFlux);
    }

    public List<FluxResponse> findAll() {
        logger.info("Récupération de tous les flux");

        List<FluxResponse> fluxList = fluxRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();

        logger.info("Nombre de flux récupérés : {}", fluxList.size());
        return fluxList;
    }

    public FluxResponse findById(Long id) {
        logger.info("Recherche du flux avec l'id={}", id);

        Flux flux = fluxRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Flux introuvable avec l'id={}", id);
                    return new FluxNotFoundException(id);
                });

        logger.info("Flux trouvé avec succès : id={}", id);
        return mapToResponse(flux);
    }

    public FluxResponse update(Long id, UpdateFluxRequest request) {
        logger.info("Mise à jour du flux id={} : type={}, libelle={}, montant={}",
                id,
                request.getType(),
                request.getLibelle(),
                request.getMontant());

        Flux flux = fluxRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Impossible de mettre à jour : flux introuvable avec l'id={}", id);
                    return new FluxNotFoundException(id);
                });

        mapUpdateRequestToEntity(request, flux);

        Flux updatedFlux = fluxRepository.save(flux);

        logger.info("Flux mis à jour avec succès : id={}", updatedFlux.getId());
        return mapToResponse(updatedFlux);
    }

    public void delete(Long id) {
        logger.info("Suppression du flux id={}", id);

        Flux flux = fluxRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Impossible de supprimer : flux introuvable avec l'id={}", id);
                    return new FluxNotFoundException(id);
                });

        fluxRepository.delete(flux);

        logger.info("Flux supprimé avec succès : id={}", id);
    }

    private void mapCreateRequestToEntity(CreateFluxRequest request, Flux flux) {
        flux.setDate(request.getDate());
        flux.setType(request.getType());
        flux.setLibelle(request.getLibelle());
        flux.setMontant(request.getMontant());
        flux.setCategorie(request.getCategorie());
        flux.setModePaiement(request.getModePaiement());
        flux.setCommentaire(request.getCommentaire());
    }

    private void mapUpdateRequestToEntity(UpdateFluxRequest request, Flux flux) {
        flux.setDate(request.getDate());
        flux.setType(request.getType());
        flux.setLibelle(request.getLibelle());
        flux.setMontant(request.getMontant());
        flux.setCategorie(request.getCategorie());
        flux.setModePaiement(request.getModePaiement());
        flux.setCommentaire(request.getCommentaire());
    }

    private FluxResponse mapToResponse(Flux flux) {
        FluxResponse response = new FluxResponse();
        response.setId(flux.getId());
        response.setDate(flux.getDate());
        response.setType(flux.getType());
        response.setLibelle(flux.getLibelle());
        response.setMontant(flux.getMontant());
        response.setCategorie(flux.getCategorie());
        response.setModePaiement(flux.getModePaiement());
        response.setCommentaire(flux.getCommentaire());
        response.setCreatedAt(flux.getCreatedAt());
        response.setUpdatedAt(flux.getUpdatedAt());

        return response;
    }
}