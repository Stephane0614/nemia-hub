package com.nemia.core.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadConfig {

    @Value("${UPLOAD_DIR}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Path justificatifsPath = Paths.get(uploadDir, "justificatifs");
        if (!Files.exists(justificatifsPath)) {
            Files.createDirectories(justificatifsPath);
            System.out.println("Dossier créé : " + justificatifsPath.toAbsolutePath());
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}