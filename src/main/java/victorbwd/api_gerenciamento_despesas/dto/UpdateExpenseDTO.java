package victorbwd.api_gerenciamento_despesas.dto;

import java.time.LocalDate;

public record UpdateExpenseDTO(
        String description,
        Double value,
        String category,
        LocalDate date
) {
    public UpdateExpenseDTO {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Value must be a positive number");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category cannot be null or blank");
        }
    }
}
