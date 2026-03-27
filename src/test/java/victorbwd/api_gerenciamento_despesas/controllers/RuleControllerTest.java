package victorbwd.api_gerenciamento_despesas.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import victorbwd.api_gerenciamento_despesas.dto.RuleRequestsDTO;
import victorbwd.api_gerenciamento_despesas.dto.RuleResponseDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.RuleService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RuleControllerTest {

    private RuleService ruleService;
    private AuthService authService;
    private RuleController controller;
    private Authentication authentication;
    private UUID userId;

    @BeforeEach
    void setup() {
        ruleService = mock(RuleService.class);
        authService = mock(AuthService.class);
        controller = new RuleController(ruleService, authService);
        authentication = mock(Authentication.class);
        userId = UUID.randomUUID();

        when(authService.extractUserIdFromAuth(authentication)).thenReturn(userId);
    }

    @Test
    void createRuleShouldReturnCreatedResponse() {
        RuleRequestsDTO dto = new RuleRequestsDTO("uber", 1L, 1);
        RuleResponseDTO expected = new RuleResponseDTO(5L, "uber", 1, "Transport", 1);

        when(ruleService.createRule(dto, userId)).thenReturn(expected);

        ResponseEntity<RuleResponseDTO> response = controller.createRule(dto, authentication);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(5L, response.getBody().id());
    }
}

