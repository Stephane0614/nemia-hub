package com.nemia.core.travaux.controller;

import com.nemia.core.travaux.dto.TravauxRequest;
import com.nemia.core.travaux.dto.TravauxResponse;
import com.nemia.core.travaux.service.TravauxService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/travaux")
public class TravauxController {

  private final TravauxService travauxService;

  public TravauxController(TravauxService travauxService) {
    this.travauxService = travauxService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TravauxResponse create(@Valid @RequestBody TravauxRequest request) {
    return travauxService.create(request);
  }

  @GetMapping
  public List<TravauxResponse> findAll(@RequestParam(required = false) Long bienId) {
    return travauxService.findAll(bienId);
  }

  @GetMapping("/{id}")
  public TravauxResponse findById(@PathVariable Long id) {
    return travauxService.findById(id);
  }

  @PutMapping("/{id}")
  public TravauxResponse update(@PathVariable Long id, @Valid @RequestBody TravauxRequest request) {
    return travauxService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    travauxService.delete(id);
  }
}
