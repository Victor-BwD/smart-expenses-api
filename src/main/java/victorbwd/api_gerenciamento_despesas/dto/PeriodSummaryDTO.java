package victorbwd.api_gerenciamento_despesas.dto;

import java.math.BigDecimal;

public record PeriodSummaryDTO(
        String period,
        BigDecimal totalAmount,
        Long transactionCount
) {
}
