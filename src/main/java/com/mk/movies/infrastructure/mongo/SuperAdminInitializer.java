package com.mk.movies.infrastructure.mongo;

import com.mk.movies.domain.user.document.User;
import com.mk.movies.domain.user.enums.Role;
import com.mk.movies.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SuperAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${SUPER_ADMIN_PASSWORD}")
    private String superAdminPassword;

    @Value("${SUPER_ADMIN_EMAIL:superadmin@example.com}")
    private String superAdminEmail;

    @Bean
    public CommandLineRunner createSuperAdmin() {
        return args -> {
            if (superAdminPassword == null || superAdminPassword.isBlank()) {
                throw new IllegalStateException("SUPER_ADMIN_PASSWORD must be set.");
            }

            if (!userRepository.existsByEmail(superAdminEmail)) {
                var superAdmin = new User(
                    null,
                    superAdminEmail,
                    "Super",
                    "Admin",
                    passwordEncoder.encode(superAdminPassword),
                    Role.SUPER_ADMIN
                );
                userRepository.save(superAdmin);
            }
        };
    }
}
