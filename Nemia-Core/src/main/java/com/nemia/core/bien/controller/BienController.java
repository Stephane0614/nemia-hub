package com.nemia.core.bien.controller;

import com.nemia.core.bien.dto.BienRequest;
import com.nemia.core.bien.dto.BienResponse;
import com.nemia.core.bien.service.BienService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/biens")
public class BienController {

  private final BienService bienService;

  public BienController(BienService bienService) {
    this.bienService = bienService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public BienResponse create(@Valid @RequestBody BienRequest request) {
    return bienService.create(request);
  }

  @GetMapping
  public List<BienResponse> findAll() {
    return bienService.findAll();
  }

  @GetMapping("/{id}")
  public BienResponse findById(@PathVariable Long id) {
    return bienService.findById(id);
  }

  @PutMapping("/{id}")
  public BienResponse update(@PathVariable Long id, @Valid @RequestBody BienRequest request) {
    return bienService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    bienService.delete(id);
  }
}