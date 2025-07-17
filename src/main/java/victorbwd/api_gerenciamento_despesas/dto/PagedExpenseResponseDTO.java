package victorbwd.api_gerenciamento_despesas.dto;

import java.util.List;

public record PagedExpenseResponseDTO(List<ExpenseResponseDTO> expenses, long totalElements, int currentPage,int pageSize, boolean hasNext, boolean hasPrevious) {
}
