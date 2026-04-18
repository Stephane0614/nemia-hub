package com.nemia.core.exercice.controller;

import com.nemia.core.exercice.dto.ExerciceRequest;
import com.nemia.core.exercice.dto.ExerciceResponse;
import com.nemia.core.exercice.service.ExerciceService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

  private final ExerciceService exerciceService;

  public ExerciceController(ExerciceService exerciceService) {
    this.exerciceService = exerciceService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ExerciceResponse create(@Valid @RequestBody ExerciceRequest request) {
    return exerciceService.create(request);
  }

  @GetMapping
  public List<ExerciceResponse> findAll() {
    return exerciceService.findAll();
  }

  @GetMapping("/{id}")
  public ExerciceResponse findById(@PathVariable Long id) {
    return exerciceService.findById(id);
  }

  @PutMapping("/{id}")
  public ExerciceResponse update(@PathVariable Long id, @Valid @RequestBody ExerciceRequest request) {
    return exerciceService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    exerciceService.delete(id);
  }
}