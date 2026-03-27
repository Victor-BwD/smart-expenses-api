package victorbwd.api_gerenciamento_despesas.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.dto.CategoryDTO;
import victorbwd.api_gerenciamento_despesas.dto.CreateCategoryDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.CategoryService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    private CategoryService categoryService;
    private AuthService authService;
    private CategoryController controller;
    private Authentication authentication;
    private UUID userId;

    @BeforeEach
    void setup() {
        categoryService = mock(CategoryService.class);
        authService = mock(AuthService.class);
        controller = new CategoryController(categoryService, authService);
        authentication = mock(Authentication.class);
        userId = UUID.randomUUID();

        when(authService.extractUserIdFromAuth(authentication)).thenReturn(userId);
    }

    @Test
    void getAllCategoriesShouldReturnOk() {
        when(categoryService.getAllCategories(userId)).thenReturn(List.of(new CategoryDTO(1, "Food", "Meals", "#fff", false)));

        ResponseEntity<List<CategoryDTO>> response = controller.getAllCategories(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createShouldReturnCreated() {
        CreateCategoryDTO dto = new CreateCategoryDTO("Food", "Meals", "#FFFFFF");
        Category category = new Category();
        category.setId(10);
        category.setName("Food");
        category.setDescription("Meals");
        category.setColor("#FFFFFF");
        category.setIsDefault(false);

        when(categoryService.create(dto, userId)).thenReturn(category);

        ResponseEntity<CategoryDTO> response = controller.create(dto, authentication);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Food", response.getBody().name());
    }

    @Test
    void deleteShouldReturnNoContent() {
        ResponseEntity<Void> response = controller.deleteCategory(10, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(categoryService).deleteCategory(10, userId);
    }
}

