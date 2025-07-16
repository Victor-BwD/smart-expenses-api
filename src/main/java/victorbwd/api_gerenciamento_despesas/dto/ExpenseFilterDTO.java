package victorbwd.api_gerenciamento_despesas.dto;

import java.time.LocalDate;

public record ExpenseFilterDTO(LocalDate startDate, LocalDate endDate, String categoryId, String description, int page, int limit) {
    public ExpenseFilterDTO {
        if (page < 0) page = 0;
        if (limit <= 0) limit = 10;
        if (limit > 100) limit = 100;
    }
}
