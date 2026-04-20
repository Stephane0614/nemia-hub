package com.nemia.core.flux.controller;

import com.nemia.core.flux.dto.CreateFluxRequest;
import com.nemia.core.flux.dto.FluxPageResponse;
import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.dto.UpdateFluxRequest;
import com.nemia.core.flux.service.FluxService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flux")
public class FluxController {

  private final FluxService fluxService;

  public FluxController(FluxService fluxService) {
    this.fluxService = fluxService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public FluxResponse create(@Valid @RequestBody CreateFluxRequest request) {
    return fluxService.create(request);
  }

  @GetMapping
  public FluxPageResponse findAll(
    @RequestParam(required = false) Long bienId,
    @RequestParam(required = false) Long exerciceId,
    @RequestParam(required = false) String typeFlux,
    @RequestParam(required = false) String categorie,
    @RequestParam(required = false) String dateDebut,
    @RequestParam(required = false) String dateFin,
    @RequestParam(required = false) String qualificationPressentie,
    @RequestParam(required = false) String statutTraitement,
    @RequestParam(required = false) String statutJustificatif,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
  ) {
    return fluxService.findAllWithFilters(
      bienId,
      exerciceId,
      typeFlux,
      categorie,
      dateDebut,
      dateFin,
      qualificationPressentie,
      statutTraitement,
      statutJustificatif,
      page,
      size
    );
  }

  @GetMapping("/{id}")
  public FluxResponse findById(@PathVariable Long id) {
    return fluxService.findById(id);
  }

  @PutMapping("/{id}")
  public FluxResponse update(@PathVariable Long id, @Valid @RequestBody UpdateFluxRequest request) {
    return fluxService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    fluxService.delete(id);
  }
}
