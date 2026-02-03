package hu.agoston.contractmanager.config;

import hu.agoston.contractmanager.domain.Role;
import hu.agoston.contractmanager.domain.User;
import hu.agoston.contractmanager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class AdminUserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserInitializer(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (!userRepository.existsByEmail("admin@local")) {
            User admin = new User(
                    "admin@local",
                    passwordEncoder.encode("admin123"),
                    Role.ROLE_ADMIN,
                    true
            );
            userRepository.save(admin);
        }
    }
}
