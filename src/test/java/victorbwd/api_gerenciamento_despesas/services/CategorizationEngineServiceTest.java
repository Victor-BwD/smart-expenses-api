package victorbwd.api_gerenciamento_despesas.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.rules.CategorizationRules;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.repositories.CategorizationRuleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategorizationEngineServiceTest {

    @Mock
    private CategorizationRuleRepository ruleRepository;

    @InjectMocks
    private CategorizationEngineService categorizationEngineService;

    @Test
    void findCategoryForRuleShouldReturnMatchingCategoryIgnoringCase() {
        User user = new User();
        Expenses expense = new Expenses();
        expense.setUser(user);
        expense.setDescription("Pagamento UBER viagem");

        Category transport = new Category();
        transport.setName("Transport");

        CategorizationRules rule = new CategorizationRules();
        rule.setKeyword("uber");
        rule.setCategory(transport);

        when(ruleRepository.findByUserOrderByPriorityAsc(user)).thenReturn(List.of(rule));

        Optional<Category> result = categorizationEngineService.findCategoryForRule(expense);

        assertTrue(result.isPresent());
        assertEquals("Transport", result.get().getName());
    }

    @Test
    void findCategoryForRuleShouldReturnEmptyWhenNoRulesMatch() {
        User user = new User();
        Expenses expense = new Expenses();
        expense.setUser(user);
        expense.setDescription("Cinema");

        CategorizationRules rule = new CategorizationRules();
        rule.setKeyword("uber");
        rule.setCategory(new Category());

        when(ruleRepository.findByUserOrderByPriorityAsc(user)).thenReturn(List.of(rule));

        Optional<Category> result = categorizationEngineService.findCategoryForRule(expense);

        assertTrue(result.isEmpty());
    }

    @Test
    void findCategoryForRuleShouldHandleNullDescription() {
        User user = new User();
        Expenses expense = new Expenses();
        expense.setUser(user);
        expense.setDescription(null);

        when(ruleRepository.findByUserOrderByPriorityAsc(user)).thenReturn(List.of());

        Optional<Category> result = categorizationEngineService.findCategoryForRule(expense);

        assertTrue(result.isEmpty());
    }
}

