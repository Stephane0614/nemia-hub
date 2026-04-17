package com.nemia.core.flux.controller;

import com.nemia.core.flux.dto.CreateFluxRequest;
import com.nemia.core.flux.dto.FluxResponse;
import com.nemia.core.flux.dto.UpdateFluxRequest;
import com.nemia.core.flux.service.FluxService;
import jakarta.validation.Valid;
import java.util.List;
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
  public List<FluxResponse> findAll() {
    return fluxService.findAll();
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
