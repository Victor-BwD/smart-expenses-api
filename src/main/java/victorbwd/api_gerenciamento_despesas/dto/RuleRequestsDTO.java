package victorbwd.api_gerenciamento_despesas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RuleRequestsDTO(
        @NotBlank String keyword,
        @NotNull Long categoryId,
        @NotNull Integer priority
) {}
