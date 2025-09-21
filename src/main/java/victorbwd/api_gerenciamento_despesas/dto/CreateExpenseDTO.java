package victorbwd.api_gerenciamento_despesas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record CreateExpenseDTO(
        @Size(max = 100, message = "Description must be up to 100 characters")
        String description,

        @NotNull(message = "Value cannot be null")
        Double value,

        @Size(min = 2, max = 50, message = "Category must be between 2 and 50 characters")
        String category,

        UUID userId,

        LocalDate date
) {

}
