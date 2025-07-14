package victorbwd.api_gerenciamento_despesas.dto;

import java.util.Date;
import java.util.UUID;

public record CreateExpenseDTO(
        String description,
        Double value,
        String category,
        UUID userId,
        Date date
) {

}
