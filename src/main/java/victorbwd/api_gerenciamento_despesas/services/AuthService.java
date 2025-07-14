package victorbwd.api_gerenciamento_despesas.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID extractUserIdFromAuth(Authentication auth) {
        String email = extractEmailFromAuth(auth);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return user.getId();
    }

    private String extractEmailFromAuth(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Authentication is not valid");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            String email = user.getEmail();
            if (email == null || email.trim().isEmpty()) {
                throw new RuntimeException("User email is null or empty");
            }
            return email;
        }

        if (principal instanceof String email) {
            return email;
        }

        throw new RuntimeException("Cannot extract user identity safely");
    }
}
