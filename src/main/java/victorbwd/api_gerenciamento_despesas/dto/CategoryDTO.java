package victorbwd.api_gerenciamento_despesas.dto;

public record CategoryDTO(
        Integer id,
        String name,
        String description,
        String color,
        Boolean isDefault) {
}
