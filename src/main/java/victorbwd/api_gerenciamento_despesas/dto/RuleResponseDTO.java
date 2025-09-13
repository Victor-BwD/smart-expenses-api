package victorbwd.api_gerenciamento_despesas.dto;

public record RuleResponseDTO(Long id,
                              String keyword,
                              Integer categoryId,
                              String categoryName,
                              Integer priority) {
}
