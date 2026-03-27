package victorbwd.api_gerenciamento_despesas.infra.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService service;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        service = new CustomUserDetailsService();
        ReflectionTestUtils.setField(service, "repository", userRepository);
    }

    @Test
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setEmail("victor@mail.com");
        user.setPassword("encoded");

        when(userRepository.findByEmail("victor@mail.com")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("victor@mail.com");

        assertEquals("victor@mail.com", details.getUsername());
        assertEquals("encoded", details.getPassword());
    }

    @Test
    void shouldThrowWhenUserIsMissing() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@mail.com"));
    }
}

