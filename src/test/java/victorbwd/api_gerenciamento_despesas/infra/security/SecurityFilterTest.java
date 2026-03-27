package victorbwd.api_gerenciamento_despesas.infra.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class SecurityFilterTest {

    private SecurityFilter filter;
    private TokenService tokenService;
    private UserRepository userRepository;
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        filter = new SecurityFilter();
        tokenService = mock(TokenService.class);
        userRepository = mock(UserRepository.class);
        filterChain = mock(FilterChain.class);

        ReflectionTestUtils.setField(filter, "tokenService", tokenService);
        ReflectionTestUtils.setField(filter, "userRepository", userRepository);
    }

    @AfterEach
    void clean() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateWhenTokenAndUserAreValid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token123");

        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User();
        user.setEmail("victor@mail.com");

        when(tokenService.validateToken("token123")).thenReturn("victor@mail.com");
        when(userRepository.findByEmail("victor@mail.com")).thenReturn(Optional.of(user));

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenTokenIsInvalid() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.validateToken("bad")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}

