package com.nemia.core.auth;

import com.nemia.core.auth.model.NemiaUser;
import com.nemia.core.auth.repository.NemiaUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NemiaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring injecte la valeur de la variable d'environnement NEMIA_ADMIN_PASSWORD
    // Si elle n'existe pas, Spring ne fournit aucun défaut → on vérifiera nous-mêmes
    @Value("${NEMIA_ADMIN_PASSWORD:}")
    private String adminPassword;

    @Value("${NEMIA_EMILIE_PASSWORD:}")
    private String emiliePassword;

    public DataInitializer(NemiaUserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Vérification au démarrage : si un mot de passe manque, on stoppe tout
        validatePassword(adminPassword, "NEMIA_ADMIN_PASSWORD");
        validatePassword(emiliePassword, "NEMIA_EMILIE_PASSWORD");

        createUserIfNotExists("admin", adminPassword);
        createUserIfNotExists("emilie", emiliePassword);
    }

    private void validatePassword(String password, String envVarName) {
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                "Variable d'environnement " + envVarName + " non définie. "
                + "Démarrage impossible — définissez-la dans le fichier .env"
            );
        }
    }

    private void createUserIfNotExists(String username, String password) {
        if (userRepository.findByUsername(username).isEmpty()) {
            NemiaUser user = new NemiaUser();
            user.setUsername(username);
            user.setPasswordHash(passwordEncoder.encode(password));
            userRepository.save(user);
            System.out.println("Compte créé : " + username);
        }
    }
}