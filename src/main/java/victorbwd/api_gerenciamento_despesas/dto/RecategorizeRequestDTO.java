package victorbwd.api_gerenciamento_despesas.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record RecategorizeRequestDTO(
        @NotEmpty
        List<Long> expenseIds,

        Long categoryId
) {
}
