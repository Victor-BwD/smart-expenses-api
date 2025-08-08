package victorbwd.api_gerenciamento_despesas.services;

import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.CategoryDTO;
import victorbwd.api_gerenciamento_despesas.dto.CreateCategoryDTO;
import victorbwd.api_gerenciamento_despesas.exceptions.CategoryNotFoundException;
import victorbwd.api_gerenciamento_despesas.exceptions.UserNotFoundException;
import victorbwd.api_gerenciamento_despesas.repositories.CategoryRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
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

    public Category create(CreateCategoryDTO dto, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Boolean existingCategory = categoryRepository.existsByNameAndUserIdOrDefault(dto.name(), userId);

        if (existingCategory) {
            throw new IllegalArgumentException("Category with the same name already exists");
        }

        Category category = new Category();
        category.setName(dto.name());
        category.setDescription(dto.description());
        category.setColor(dto.color());
        category.setUser(user);
        category.setIsDefault(false);

        return categoryRepository.save(category);
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

    public void deleteCategory(Integer categoryId, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new CategoryNotFoundException("Category not found");
        }

        Category category = categoryOptional.get();
        if (!category.getUser().getId().equals(user.getId())) {
            throw new CategoryNotFoundException("Category not found");
        }

        categoryRepository.delete(category);
    }
}
