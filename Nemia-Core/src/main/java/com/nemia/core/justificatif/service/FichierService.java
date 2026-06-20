package com.nemia.core.justificatif.service;

import com.nemia.core.config.UploadConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
public class FichierService {

    private static final Set<String> TYPES_AUTORISES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );
    private static final long TAILLE_MAX = 5 * 1024 * 1024L; // 5 Mo

    private final UploadConfig uploadConfig;

    public FichierService(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }

    public void valider(MultipartFile fichier) throws IOException {
        if (fichier.getSize() > TAILLE_MAX) {
            throw new IllegalArgumentException(
                "Fichier trop volumineux : taille max 5 Mo");
        }

        String typeMime = detecterTypeMime(fichier);
        if (!TYPES_AUTORISES.contains(typeMime)) {
            throw new IllegalArgumentException(
                "Type de fichier non autorisé : " + typeMime +
                ". Formats acceptés : PDF, JPG, PNG");
        }
    }

    public String stocker(Long justificatifId, MultipartFile fichier) throws IOException {
        String typeMime = detecterTypeMime(fichier);
        String nomOriginal = fichier.getOriginalFilename() != null
                ? fichier.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_")
                : "fichier";

        String nomStocke = justificatifId + "_" + System.currentTimeMillis() + "_" + nomOriginal;

        Path dossier = Paths.get(uploadConfig.getUploadDir(), "justificatifs");
        Path destination = dossier.resolve(nomStocke);

        Files.copy(fichier.getInputStream(), destination);

        return destination.toString();
    }

    public void supprimer(String chemin) {
        if (chemin == null) return;
        try {
            Files.deleteIfExists(Paths.get(chemin));
        } catch (IOException e) {
            System.err.println("Impossible de supprimer le fichier : " + chemin);
        }
    }

    public Path resoudre(String chemin) {
        return Paths.get(chemin);
    }

    private String detecterTypeMime(MultipartFile fichier) throws IOException {
        byte[] bytes = fichier.getBytes();
        if (bytes.length >= 4) {
            // PDF : %PDF
            if (bytes[0] == 0x25 && bytes[1] == 0x50 &&
                bytes[2] == 0x44 && bytes[3] == 0x46) {
                return "application/pdf";
            }
            // JPEG : FF D8 FF
            if ((bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xD8 &&
                (bytes[2] & 0xFF) == 0xFF) {
                return "image/jpeg";
            }
            // PNG : 89 50 4E 47
            if ((bytes[0] & 0xFF) == 0x89 && bytes[1] == 0x50 &&
                bytes[2] == 0x4E && bytes[3] == 0x47) {
                return "image/png";
            }
        }
        throw new IllegalArgumentException(
            "Type de fichier non reconnu. Formats acceptés : PDF, JPG, PNG");
    }
}