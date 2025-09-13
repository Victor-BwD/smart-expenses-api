package victorbwd.api_gerenciamento_despesas.services;

import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.rules.CategorizationRules;
import victorbwd.api_gerenciamento_despesas.repositories.CategorizationRuleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategorizationEngineService {
    private final CategorizationRuleRepository ruleRepository;

    public CategorizationEngineService(CategorizationRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public Optional<Category> findCategoryForRule(Expenses expense) {
        List<CategorizationRules> userRules = ruleRepository.findByUserOrderByPriorityAsc(expense.getUser());

        String descriptionLowerCase = expense.getDescription() == null ? "" : expense.getDescription().toLowerCase();

        for(CategorizationRules rule : userRules) {
            if(descriptionLowerCase.contains(rule.getKeyword().toLowerCase())) {
                return Optional.of(rule.getCategory());
            }
        }

        return Optional.empty();
    }
}
