package victorbwd.api_gerenciamento_despesas.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.rules.CategorizationRules;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.RuleRequestsDTO;
import victorbwd.api_gerenciamento_despesas.dto.RuleResponseDTO;
import victorbwd.api_gerenciamento_despesas.exceptions.CategoryNotFoundException;
import victorbwd.api_gerenciamento_despesas.exceptions.UserNotFoundException;
import victorbwd.api_gerenciamento_despesas.repositories.CategorizationRuleRepository;
import victorbwd.api_gerenciamento_despesas.repositories.CategoryRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {

    @Mock
    private CategorizationRuleRepository ruleRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RuleService ruleService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
    }

    @Test
    void createRuleShouldReturnMappedResponse() {
        RuleRequestsDTO dto = new RuleRequestsDTO("uber", 1L, 1);

        Category category = new Category();
        category.setId(1);
        category.setName("Transport");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));
        when(ruleRepository.save(any(CategorizationRules.class))).thenAnswer(invocation -> {
            CategorizationRules rule = invocation.getArgument(0);
            rule.setId(100L);
            return rule;
        });

        RuleResponseDTO result = ruleService.createRule(dto, userId);

        assertEquals(100L, result.id());
        assertEquals("uber", result.keyword());
        assertEquals("Transport", result.categoryName());
        assertEquals(1, result.priority());
    }

    @Test
    void createRuleShouldThrowWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> ruleService.createRule(new RuleRequestsDTO("uber", 1L, 1), userId));
    }

    @Test
    void createRuleShouldThrowWhenCategoryNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class,
                () -> ruleService.createRule(new RuleRequestsDTO("uber", 1L, 1), userId));
    }
}

