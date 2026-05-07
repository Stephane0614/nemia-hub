package com.nemia.core.auth;

import com.nemia.core.auth.model.NemiaUser;
import com.nemia.core.auth.repository.NemiaUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NemiaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(NemiaUserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUserIfNotExists("admin", "admin2026");
        createUserIfNotExists("emilie", "emilie2026");
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