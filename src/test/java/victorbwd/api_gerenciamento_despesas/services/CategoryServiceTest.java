package victorbwd.api_gerenciamento_despesas.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setName("Victor");
    }

    @Test
    void getAllCategoriesShouldReturnMappedDtos() {
        Category category = new Category();
        category.setId(1);
        category.setName("Food");
        category.setDescription("Meals");
        category.setColor("#FF0000");
        category.setIsDefault(false);

        when(categoryRepository.findByUserIdOrDefault(userId)).thenReturn(List.of(category));

        List<CategoryDTO> result = categoryService.getAllCategories(userId);

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).name());
    }

    @Test
    void getAllCategoriesShouldThrowWhenEmpty() {
        when(categoryRepository.findByUserIdOrDefault(userId)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> categoryService.getAllCategories(userId));
    }

    @Test
    void createShouldThrowWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> categoryService.create(new CreateCategoryDTO("Food", "Meals", "#FFFFFF"), userId));
    }

    @Test
    void createShouldThrowWhenCategoryAlreadyExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndUserIdOrDefault("Food", userId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> categoryService.create(new CreateCategoryDTO("Food", "Meals", "#FFFFFF"), userId));
    }

    @Test
    void createShouldSaveCategoryWithExpectedFields() {
        CreateCategoryDTO dto = new CreateCategoryDTO("Travel", "Trips", "#00FF00");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByNameAndUserIdOrDefault("Travel", userId)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category result = categoryService.create(dto, userId);

        assertEquals("Travel", result.getName());
        assertEquals("Trips", result.getDescription());
        assertEquals("#00FF00", result.getColor());
        assertFalse(result.getIsDefault());
        assertEquals(user, result.getUser());
    }

    @Test
    void deleteCategoryShouldThrowWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> categoryService.deleteCategory(1, userId));
    }

    @Test
    void deleteCategoryShouldThrowWhenCategoryNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1, userId));
    }

    @Test
    void deleteCategoryShouldThrowWhenCategoryHasNoOwner() {
        Category category = new Category();
        category.setId(1);
        category.setUser(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1, userId));
    }

    @Test
    void deleteCategoryShouldThrowWhenCategoryBelongsToAnotherUser() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());

        Category category = new Category();
        category.setId(1);
        category.setUser(anotherUser);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(1, userId));
    }

    @Test
    void deleteCategoryShouldDeleteWhenOwnedByUser() {
        Category category = new Category();
        category.setId(1);
        category.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1, userId);

        verify(categoryRepository).delete(category);
    }
}

