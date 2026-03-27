package victorbwd.api_gerenciamento_despesas.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.exceptions.InvalidAuthenticationException;
import victorbwd.api_gerenciamento_despesas.exceptions.UserNotFoundException;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    void extractUserIdFromAuthShouldWorkWithUserPrincipal() {
        User principal = new User();
        principal.setEmail("victor@mail.com");

        User found = new User();
        found.setId(userId);
        found.setEmail("victor@mail.com");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);
        when(userRepository.findByEmail("victor@mail.com")).thenReturn(Optional.of(found));

        UUID result = authService.extractUserIdFromAuth(auth);

        assertEquals(userId, result);
    }

    @Test
    void extractUserIdFromAuthShouldWorkWithStringPrincipal() {
        User found = new User();
        found.setId(userId);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("victor@mail.com");
        when(userRepository.findByEmail("victor@mail.com")).thenReturn(Optional.of(found));

        UUID result = authService.extractUserIdFromAuth(auth);

        assertEquals(userId, result);
    }

    @Test
    void extractUserIdFromAuthShouldThrowWhenAuthIsNull() {
        assertThrows(InvalidAuthenticationException.class, () -> authService.extractUserIdFromAuth(null));
    }

    @Test
    void extractUserIdFromAuthShouldThrowWhenNotAuthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        assertThrows(InvalidAuthenticationException.class, () -> authService.extractUserIdFromAuth(auth));
    }

    @Test
    void extractUserIdFromAuthShouldThrowWhenUserPrincipalEmailIsBlank() {
        User principal = new User();
        principal.setEmail(" ");

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(principal);

        assertThrows(RuntimeException.class, () -> authService.extractUserIdFromAuth(auth));
    }

    @Test
    void extractUserIdFromAuthShouldThrowWhenPrincipalTypeIsUnsupported() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(123L);

        assertThrows(RuntimeException.class, () -> authService.extractUserIdFromAuth(auth));
    }

    @Test
    void extractUserIdFromAuthShouldThrowWhenUserIsNotFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn("missing@mail.com");
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.extractUserIdFromAuth(auth));
    }

    @Test
    void getUserByIdShouldThrowWhenMissing() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.getUserById(userId));
    }
}

