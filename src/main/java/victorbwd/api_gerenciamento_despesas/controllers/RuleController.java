package victorbwd.api_gerenciamento_despesas.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import victorbwd.api_gerenciamento_despesas.dto.ResponseDTO;
import victorbwd.api_gerenciamento_despesas.dto.RuleRequestsDTO;
import victorbwd.api_gerenciamento_despesas.dto.RuleResponseDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.RuleService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/rules")
public class RuleController {
    private final RuleService ruleService;
    private final AuthService authService;

    public RuleController(RuleService ruleService, AuthService authService) {
        this.ruleService = ruleService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<RuleResponseDTO> createRule(@RequestBody @Valid RuleRequestsDTO dto, Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);
        RuleResponseDTO createRule = ruleService.createRule(dto, userId);

        URI uri = URI.create("/rules/" + createRule.id());

        RuleResponseDTO responseDTO = new RuleResponseDTO(
                createRule.id(),
                createRule.keyword(),
                createRule.categoryId(),
                createRule.categoryName(),
                createRule.priority()
        );

        return ResponseEntity.created(uri).body(responseDTO);
    }
}
