package victorbwd.api_gerenciamento_despesas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.dto.CategoryDTO;
import victorbwd.api_gerenciamento_despesas.dto.CreateCategoryDTO;
import victorbwd.api_gerenciamento_despesas.dto.CreateExpenseDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.CategoryService;

import java.net.URI;
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

    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> create(@RequestBody CreateCategoryDTO dto, Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);

        Category categoryDTO = categoryService.create(dto, userId);

        URI uri = URI.create("/categories/" + categoryDTO.getId());
        CategoryDTO responseDTO = new CategoryDTO(
                categoryDTO.getId(),
                categoryDTO.getName(),
                categoryDTO.getDescription(),
                categoryDTO.getColor(),
                categoryDTO.getIsDefault()
        );

        return ResponseEntity.created(uri).body(responseDTO);
    }
}
