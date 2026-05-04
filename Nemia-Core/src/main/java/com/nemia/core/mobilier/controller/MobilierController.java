package com.nemia.core.mobilier.controller;

import com.nemia.core.mobilier.dto.MobilierRequest;
import com.nemia.core.mobilier.dto.MobilierResponse;
import com.nemia.core.mobilier.service.MobilierService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobilier")
public class MobilierController {

  private final MobilierService mobilierService;

  public MobilierController(MobilierService mobilierService) {
    this.mobilierService = mobilierService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MobilierResponse create(@Valid @RequestBody MobilierRequest request) {
    return mobilierService.create(request);
  }

  @GetMapping
  public List<MobilierResponse> findAll(@RequestParam(required = false) Long bienId) {
    return mobilierService.findAll(bienId);
  }

  @GetMapping("/{id}")
  public MobilierResponse findById(@PathVariable Long id) {
    return mobilierService.findById(id);
  }

  @PutMapping("/{id}")
  public MobilierResponse update(@PathVariable Long id, @Valid @RequestBody MobilierRequest request) {
    return mobilierService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    mobilierService.delete(id);
  }
}
