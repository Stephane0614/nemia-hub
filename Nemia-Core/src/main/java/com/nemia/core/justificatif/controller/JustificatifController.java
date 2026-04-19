package com.nemia.core.justificatif.controller;

import com.nemia.core.justificatif.dto.JustificatifRequest;
import com.nemia.core.justificatif.dto.JustificatifResponse;
import com.nemia.core.justificatif.service.JustificatifService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/justificatifs")
public class JustificatifController {

  private final JustificatifService justificatifService;

  public JustificatifController(JustificatifService justificatifService) {
    this.justificatifService = justificatifService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public JustificatifResponse create(@Valid @RequestBody JustificatifRequest request) {
    return justificatifService.create(request);
  }

  @GetMapping
  public List<JustificatifResponse> findAll() {
    return justificatifService.findAll();
  }

  @GetMapping("/{id}")
  public JustificatifResponse findById(@PathVariable Long id) {
    return justificatifService.findById(id);
  }

  @PutMapping("/{id}")
  public JustificatifResponse update(@PathVariable Long id, @Valid @RequestBody JustificatifRequest request) {
    return justificatifService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    justificatifService.delete(id);
  }
}