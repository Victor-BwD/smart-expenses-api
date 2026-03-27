package victorbwd.api_gerenciamento_despesas.infra.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import victorbwd.api_gerenciamento_despesas.domain.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret-key");
    }

    @Test
    void shouldGenerateAndValidateToken() {
        User user = new User();
        user.setEmail("victor@mail.com");

        String token = tokenService.generateToken(user);
        String subject = tokenService.validateToken(token);

        assertNotNull(token);
        assertEquals("victor@mail.com", subject);
    }

    @Test
    void shouldReturnNullWhenTokenIsInvalid() {
        assertNull(tokenService.validateToken("invalid.token"));
    }
}

