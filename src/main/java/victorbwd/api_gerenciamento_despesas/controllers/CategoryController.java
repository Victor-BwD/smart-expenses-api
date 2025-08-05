package victorbwd.api_gerenciamento_despesas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victorbwd.api_gerenciamento_despesas.dto.CategoryDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.CategoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final AuthService authService;

    public CategoryController(CategoryService categoryService, AuthService authService) {
        this.categoryService = categoryService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);
        List<CategoryDTO> categories = categoryService.getAllCategories(userId);

        return ResponseEntity.ok(categories);
    }
}
