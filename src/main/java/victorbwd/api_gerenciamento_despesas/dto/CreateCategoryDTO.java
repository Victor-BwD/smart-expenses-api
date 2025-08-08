package victorbwd.api_gerenciamento_despesas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryDTO(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
        String name,
        @Size(max = 100, message = "Description must be up to 100 characters")
        String description,
        @NotBlank(message = "Color cannot be blank")
        @Size(min = 7, max = 7, message = "Color must be exactly 7 characters (e.g., #FFFFFF)")
        String color
) {

}
