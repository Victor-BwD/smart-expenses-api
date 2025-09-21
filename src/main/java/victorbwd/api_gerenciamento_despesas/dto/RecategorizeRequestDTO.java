package victorbwd.api_gerenciamento_despesas.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RecategorizeRequestDTO(
        @NotEmpty
        @Size(max = 50)
        List<Long> expenseIds,

        Long categoryId
) {
}
