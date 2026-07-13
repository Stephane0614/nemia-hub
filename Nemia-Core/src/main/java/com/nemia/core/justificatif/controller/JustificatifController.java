package com.nemia.core.justificatif.controller;

import com.nemia.core.justificatif.dto.JustificatifRequest;
import com.nemia.core.justificatif.dto.JustificatifResponse;
import com.nemia.core.justificatif.model.Justificatif;
import com.nemia.core.justificatif.service.JustificatifService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.nemia.core.justificatif.service.FichierService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/justificatifs")
public class JustificatifController {

  private final JustificatifService justificatifService;
  private final FichierService fichierService;

  public JustificatifController(JustificatifService justificatifService, FichierService fichierService) {
    this.justificatifService = justificatifService;
    this.fichierService = fichierService;
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
public ResponseEntity<Void> deleteJustificatif(@PathVariable Long id) {
    Justificatif justificatif = justificatifService.findEntityById(id);
    fichierService.supprimer(justificatif.getFichierChemin());
    justificatifService.delete(id);
    return ResponseEntity.noContent().build();
}

  // POST /api/justificatifs/{id}/fichier
@PostMapping("/{id}/fichier")
public ResponseEntity<JustificatifResponse> uploadFichier(
        @PathVariable Long id,
        @RequestParam("fichier") MultipartFile fichier) throws IOException {

    Justificatif justificatif = justificatifService.findEntityById(id);

    if (justificatif.getFichierChemin() != null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Un fichier est déjà attaché. Supprimez-le avant d'en uploader un nouveau.");
    }

    fichierService.valider(fichier);
    String chemin = fichierService.stocker(id, fichier);
    String typeMime = fichier.getContentType();

    justificatif.setFichierNom(fichier.getOriginalFilename());
    justificatif.setFichierChemin(chemin);
    justificatif.setFichierType(typeMime);
    justificatif.setFichierTaille(fichier.getSize());

    JustificatifResponse response = justificatifService.save(justificatif);
    return ResponseEntity.ok(response);
}

// GET /api/justificatifs/{id}/fichier
@GetMapping("/{id}/fichier")
public ResponseEntity<Resource> downloadFichier(@PathVariable Long id) throws IOException {
    Justificatif justificatif = justificatifService.findEntityById(id);

    if (justificatif.getFichierChemin() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Aucun fichier attaché à ce justificatif.");
    }

    Path path = fichierService.resoudre(justificatif.getFichierChemin());
    Resource resource = new UrlResource(path.toUri());

    if (!resource.exists()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Fichier introuvable sur le serveur.");
    }

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(justificatif.getFichierType()))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + justificatif.getFichierNom() + "\"")
            .body(resource);
}

// DELETE /api/justificatifs/{id}/fichier
@DeleteMapping("/{id}/fichier")
public ResponseEntity<JustificatifResponse> deleteFichier(@PathVariable Long id) {
    Justificatif justificatif = justificatifService.findEntityById(id);

    if (justificatif.getFichierChemin() == null) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Aucun fichier attaché à ce justificatif.");
    }

    fichierService.supprimer(justificatif.getFichierChemin());

    justificatif.setFichierNom(null);
    justificatif.setFichierChemin(null);
    justificatif.setFichierType(null);
    justificatif.setFichierTaille(null);

    JustificatifResponse response = justificatifService.save(justificatif);
    return ResponseEntity.ok(response);
}
}
