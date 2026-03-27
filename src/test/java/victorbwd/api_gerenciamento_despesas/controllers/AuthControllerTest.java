package victorbwd.api_gerenciamento_despesas.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.LoginRequestDTO;
import victorbwd.api_gerenciamento_despesas.dto.RegisterRequestDTO;
import victorbwd.api_gerenciamento_despesas.dto.ResponseDTO;
import victorbwd.api_gerenciamento_despesas.exceptions.UserAlreadyExistsException;
import victorbwd.api_gerenciamento_despesas.infra.security.TokenService;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;
    private AuthController controller;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        tokenService = mock(TokenService.class);
        controller = new AuthController(userRepository, passwordEncoder, tokenService);
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        User user = new User();
        user.setName("Victor");
        user.setEmail("victor@mail.com");
        user.setPassword("encoded");

        when(userRepository.findByEmail("victor@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", "encoded")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("token");

        ResponseEntity response = controller.login(new LoginRequestDTO("victor@mail.com", "123"));

        ResponseDTO body = (ResponseDTO) response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertEquals("token", body.token());
    }

    @Test
    void loginShouldThrowWhenPasswordIsInvalid() {
        User user = new User();
        user.setEmail("victor@mail.com");
        user.setPassword("encoded");

        when(userRepository.findByEmail("victor@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> controller.login(new LoginRequestDTO("victor@mail.com", "wrong")));
    }

    @Test
    void registerShouldThrowWhenUserAlreadyExists() {
        when(userRepository.findByEmail("victor@mail.com")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class,
                () -> controller.register(new RegisterRequestDTO("Victor", "victor@mail.com", "123")));
    }

    @Test
    void registerShouldPersistAndReturnToken() {
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.generateToken(any(User.class))).thenReturn("token");

        ResponseEntity response = controller.register(new RegisterRequestDTO("Victor", "new@mail.com", "123"));

        ResponseDTO body = (ResponseDTO) response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertEquals("token", body.token());
    }
}

