package com.nemia.core.emprunt.controller;

import com.nemia.core.emprunt.dto.EmpruntRequest;
import com.nemia.core.emprunt.dto.EmpruntResponse;
import com.nemia.core.emprunt.service.EmpruntService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

  private final EmpruntService empruntService;

  public EmpruntController(EmpruntService empruntService) {
    this.empruntService = empruntService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EmpruntResponse create(@Valid @RequestBody EmpruntRequest request) {
    return empruntService.create(request);
  }

  @GetMapping
  public List<EmpruntResponse> findAll(@RequestParam(required = false) Long bienId) {
    return empruntService.findAll(bienId);
  }

  @GetMapping("/{id}")
  public EmpruntResponse findById(@PathVariable Long id) {
    return empruntService.findById(id);
  }

  @PutMapping("/{id}")
  public EmpruntResponse update(@PathVariable Long id, @Valid @RequestBody EmpruntRequest request) {
    return empruntService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    empruntService.delete(id);
  }
}
