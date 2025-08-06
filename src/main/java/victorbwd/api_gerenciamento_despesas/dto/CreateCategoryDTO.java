package victorbwd.api_gerenciamento_despesas.dto;

public record CreateCategoryDTO(
        String name,
        String description,
        String color,
        Boolean isDefault
) {
    public CreateCategoryDTO {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Color cannot be null or blank");
        }
    }
}
