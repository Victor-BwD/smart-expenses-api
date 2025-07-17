package victorbwd.api_gerenciamento_despesas.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateExpenseDTO(
        String description,
        Double value,
        String category,
        UUID userId,
        LocalDate date
) {

}
