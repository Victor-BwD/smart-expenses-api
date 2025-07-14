package victorbwd.api_gerenciamento_despesas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpenseResponseDTO(Long id, String description, BigDecimal amount, String category, LocalDate date, String userName) {

}
