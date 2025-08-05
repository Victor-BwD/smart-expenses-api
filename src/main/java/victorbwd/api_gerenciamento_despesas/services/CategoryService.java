package victorbwd.api_gerenciamento_despesas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.dto.CategoryDTO;
import victorbwd.api_gerenciamento_despesas.repositories.CategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(String name, String description, String color, UUID userId) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setColor(color);
        category.setUserId(userId);
        category.setIsDefault(false);

        return categoryRepository.save(category);
    }

    public List<CategoryDTO> getAllCategories(UUID userId) {
        List<Category> categories = categoryRepository.findByUserIdOrDefault(userId);

        if (categories.isEmpty()) {
            throw new IllegalArgumentException("No categories found for user");
        }

        return categories.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getColor(),
                category.getIsDefault()
        );
    }
}
