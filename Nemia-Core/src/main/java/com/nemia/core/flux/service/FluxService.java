package com.nemia.core.flux.service;

import com.nemia.core.common.exception.FluxNotFoundException;
import com.nemia.core.flux.dto.CreateFluxRequest;
import com.nemia.core.flux.dto.FluxPageResponse;
import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.dto.UpdateFluxRequest;
import com.nemia.core.flux.model.Flux;
import com.nemia.core.flux.model.FluxCategory;
import com.nemia.core.flux.model.FluxType;
import com.nemia.core.flux.model.QualificationPressentie;
import com.nemia.core.flux.model.StatutJustificatif;
import com.nemia.core.flux.model.StatutTraitement;
import com.nemia.core.flux.repository.FluxRepository;
import com.nemia.core.justificatif.repository.JustificatifRepository;
import com.nemia.core.emprunt.repository.EmpruntRepository;
import com.nemia.core.mobilier.repository.MobilierRepository;
import com.nemia.core.travaux.repository.TravauxRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FluxService {

  private static final Logger logger = LoggerFactory.getLogger(FluxService.class);

  private final FluxRepository fluxRepository;
  private final FluxValidationService fluxValidationService;
  private final JustificatifRepository justificatifRepository;
  private final TravauxRepository travauxRepository;
  private final MobilierRepository mobilierRepository;
  private final EmpruntRepository empruntRepository;

  public FluxService(
    FluxRepository fluxRepository,
    FluxValidationService fluxValidationService,
    JustificatifRepository justificatifRepository,
    TravauxRepository travauxRepository,
    MobilierRepository mobilierRepository,
    EmpruntRepository empruntRepository
  ) {
    this.fluxRepository = fluxRepository;
    this.fluxValidationService = fluxValidationService;
    this.justificatifRepository = justificatifRepository;
    this.travauxRepository = travauxRepository;
    this.mobilierRepository = mobilierRepository;
    this.empruntRepository = empruntRepository;
  }

  private List<String> validateFlux(Flux flux) {
    fluxValidationService.validateBlocking(flux.getType(), flux.getCategorie());
    return fluxValidationService.computeWarnings(
      flux.getType(),
      flux.getCategorie(),
      flux.getQualificationPressentie(),
      flux.getStatutJustificatif(),
      flux.getOccurrence(),
      flux.getStatutTraitement()
    );
  }

  public FluxResponse create(CreateFluxRequest request) {
    Flux flux = new Flux();
    mapCreateRequestToEntity(request, flux);
    validateJustificatifId(flux.getJustificatifId());
    validateTravauxId(flux.getTravauxId());
    validateMobilierId(flux.getMobilierId());
    validateEmpruntId(flux.getEmpruntId());
    List<String> warnings = validateFlux(flux);
    Flux savedFlux = fluxRepository.save(flux);
    FluxResponse response = mapToResponse(savedFlux);
    response.setWarnings(warnings);
    return response;
  }

  public List<FluxResponse> findAll() {
    logger.info("Récupération de tous les flux");

    List<FluxResponse> fluxList = fluxRepository.findAll().stream().map(this::mapToResponse).toList();

    logger.info("Nombre de flux récupérés : {}", fluxList.size());
    return fluxList;
  }

  public FluxResponse findById(Long id) {
    logger.info("Recherche du flux avec l'id={}", id);

    Flux flux = fluxRepository
      .findById(id)
      .orElseThrow(() -> {
        logger.warn("Flux introuvable avec l'id={}", id);
        return new FluxNotFoundException(id);
      });

    logger.info("Flux trouvé avec succès : id={}", id);
    return mapToResponse(flux);
  }

  public FluxResponse update(Long id, UpdateFluxRequest request) {
    Flux flux = fluxRepository.findById(id).orElseThrow(() -> new FluxNotFoundException(id));
    mapUpdateRequestToEntity(request, flux);
    validateJustificatifId(flux.getJustificatifId());
    validateTravauxId(flux.getTravauxId());
    validateMobilierId(flux.getMobilierId());
    validateEmpruntId(flux.getEmpruntId());
    List<String> warnings = validateFlux(flux);
    Flux updatedFlux = fluxRepository.save(flux);
    FluxResponse response = mapToResponse(updatedFlux);
    response.setWarnings(warnings);
    return response;
  }

  public void delete(Long id) {
    logger.info("Suppression du flux id={}", id);

    Flux flux = fluxRepository
      .findById(id)
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
    flux.setLibelle(normalizeRequiredText(request.getLibelle()));
    flux.setMontant(request.getMontant());
    flux.setCategorie(request.getCategorie());
    flux.setModePaiement(request.getModePaiement());
    flux.setCommentaire(normalizeOptionalText(request.getCommentaire()));
    flux.setBienId(request.getBienId());
    flux.setExerciceId(request.getExerciceId());
    flux.setDateValeur(request.getDateValeur());
    flux.setOccurrence(request.getOccurrence());
    flux.setStatutJustificatif(request.getStatutJustificatif());
    flux.setQualificationPressentie(request.getQualificationPressentie());
    flux.setStatutTraitement(request.getStatutTraitement());
    flux.setTravauxId(request.getTravauxId());
    flux.setMobilierId(request.getMobilierId());
    flux.setEmpruntId(request.getEmpruntId());
    flux.setJustificatifId(request.getJustificatifId());
  }

  private void mapUpdateRequestToEntity(UpdateFluxRequest request, Flux flux) {
    flux.setDate(request.getDate());
    flux.setType(request.getType());
    flux.setLibelle(normalizeRequiredText(request.getLibelle()));
    flux.setMontant(request.getMontant());
    flux.setCategorie(request.getCategorie());
    flux.setModePaiement(request.getModePaiement());
    flux.setCommentaire(normalizeOptionalText(request.getCommentaire()));
    flux.setBienId(request.getBienId());
    flux.setExerciceId(request.getExerciceId());
    flux.setDateValeur(request.getDateValeur());
    flux.setOccurrence(request.getOccurrence());
    flux.setStatutJustificatif(request.getStatutJustificatif());
    flux.setQualificationPressentie(request.getQualificationPressentie());
    flux.setStatutTraitement(request.getStatutTraitement());
    flux.setTravauxId(request.getTravauxId());
    flux.setMobilierId(request.getMobilierId());
    flux.setEmpruntId(request.getEmpruntId());
    flux.setJustificatifId(request.getJustificatifId());
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
    response.setBienId(flux.getBienId());
    response.setExerciceId(flux.getExerciceId());
    response.setDateValeur(flux.getDateValeur());
    response.setOccurrence(flux.getOccurrence());
    response.setStatutJustificatif(flux.getStatutJustificatif());
    response.setQualificationPressentie(flux.getQualificationPressentie());
    response.setStatutTraitement(flux.getStatutTraitement());
    response.setTravauxId(flux.getTravauxId());
    response.setMobilierId(flux.getMobilierId());
    response.setEmpruntId(flux.getEmpruntId());
    response.setJustificatifId(flux.getJustificatifId());

    return response;
  }

  private String normalizeRequiredText(String value) {
    return value.trim();
  }

  private String normalizeOptionalText(String value) {
    if (value == null) {
      return null;
    }

    String normalizedValue = value.trim();

    return normalizedValue.isEmpty() ? null : normalizedValue;
  }

  public FluxPageResponse findAllWithFilters(
    Long bienId,
    Long exerciceId,
    String typeFluxStr,
    String categorieStr,
    String dateDebutStr,
    String dateFinStr,
    String qualificationPressentieStr,
    String statutTraitementStr,
    String statutsJustificatifStr,
    int page,
    int size
  ) {
    FluxType typeFlux = null;
    if (typeFluxStr != null && !typeFluxStr.isBlank()) {
      typeFlux = FluxType.valueOf(typeFluxStr);
    }

    FluxCategory categorie = null;
    if (categorieStr != null && !categorieStr.isBlank()) {
      categorie = FluxCategory.valueOf(categorieStr);
    }

    LocalDate dateDebut = null;
    if (dateDebutStr != null && !dateDebutStr.isBlank()) {
      dateDebut = LocalDate.parse(dateDebutStr);
    }

    LocalDate dateFin = null;
    if (dateFinStr != null && !dateFinStr.isBlank()) {
      dateFin = LocalDate.parse(dateFinStr);
    }

    QualificationPressentie qualificationPressentie = null;
    if (qualificationPressentieStr != null && !qualificationPressentieStr.isBlank()) {
      qualificationPressentie = QualificationPressentie.valueOf(qualificationPressentieStr);
    }

    StatutTraitement statutTraitement = null;
    if (statutTraitementStr != null && !statutTraitementStr.isBlank()) {
      statutTraitement = StatutTraitement.valueOf(statutTraitementStr);
    }

    List<StatutJustificatif> statutsJustificatif = null;
    if (statutsJustificatifStr != null && !statutsJustificatifStr.isBlank()) {
      statutsJustificatif = Arrays.stream(statutsJustificatifStr.split(","))
        .map(String::trim)
        .map(StatutJustificatif::valueOf)
        .collect(Collectors.toList());
    }

    Pageable pageable = PageRequest.of(page, size);

    Page<Flux> resultPage = fluxRepository.findAllWithFilters(
      bienId,
      exerciceId,
      typeFlux,
      categorie,
      dateDebut,
      dateFin,
      qualificationPressentie,
      statutTraitement,
      statutsJustificatif,
      pageable
    );

    List<FluxResponse> contenu = resultPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList());

    return new FluxPageResponse(
      contenu,
      resultPage.getNumber(),
      resultPage.getSize(),
      resultPage.getTotalElements(),
      resultPage.getTotalPages()
    );
  }

  private void validateJustificatifId(Long justificatifId) {
    if (justificatifId != null && !justificatifRepository.existsById(justificatifId)) {
      throw new IllegalArgumentException("Justificatif introuvable avec l'id : " + justificatifId);
    }
  }

  private void validateTravauxId(Long travauxId) {
    if (travauxId != null && !travauxRepository.existsById(travauxId)) {
      throw new IllegalArgumentException("Travaux introuvable avec l'id : " + travauxId);
    }
  }

  private void validateMobilierId(Long mobilierId) {
    if (mobilierId != null && !mobilierRepository.existsById(mobilierId)) {
      throw new IllegalArgumentException("Mobilier introuvable avec l'id : " + mobilierId);
    }
  }

  private void validateEmpruntId(Long empruntId) {
    if (empruntId != null && !empruntRepository.existsById(empruntId)) {
      throw new IllegalArgumentException("Emprunt introuvable avec l'id : " + empruntId);
    }
  }
}
