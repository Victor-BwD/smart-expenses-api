package victorbwd.api_gerenciamento_despesas.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.LoginRequestDTO;
import victorbwd.api_gerenciamento_despesas.dto.RegisterRequestDTO;
import victorbwd.api_gerenciamento_despesas.dto.ResponseDTO;
import victorbwd.api_gerenciamento_despesas.infra.security.TokenService;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {
        User user = this.userRepository.findByEmail(body.email()).orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid e-mail or password");
        }

        String token = this.tokenService.generateToken(user);
        return ResponseEntity.ok(new ResponseDTO(user.getName(), token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
        Optional<User> user = this.userRepository.findByEmail(body.email());
        if(user.isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(body.password()));
        newUser.setEmail(body.email());
        newUser.setName(body.name());

        this.userRepository.save(newUser);

        String token = this.tokenService.generateToken(newUser);
        return ResponseEntity.ok(new ResponseDTO(newUser.getName(), token));
    }
}
