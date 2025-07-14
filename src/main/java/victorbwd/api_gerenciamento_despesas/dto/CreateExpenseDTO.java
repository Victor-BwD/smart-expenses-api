package victorbwd.api_gerenciamento_despesas.dto;

import victorbwd.api_gerenciamento_despesas.domain.user.User;

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
