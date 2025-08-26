package victorbwd.api_gerenciamento_despesas.services;

import org.springframework.stereotype.Service;
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

import java.util.UUID;

@Service
public class RuleService {
    private final CategorizationRuleRepository ruleRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public RuleService(CategorizationRuleRepository ruleRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.ruleRepository = ruleRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public RuleResponseDTO createRule(RuleRequestsDTO dto, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        Category category = categoryRepository.findById(dto.categoryId().intValue()).orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        CategorizationRules newRule = new CategorizationRules();
        newRule.setKeyword(dto.keyword());
        newRule.setCategory(category);
        newRule.setUser(user);
        newRule.setPriority(dto.priority());

        CategorizationRules savedRule = ruleRepository.save(newRule);
        return toResponseDTO(savedRule);

    }

    private RuleResponseDTO toResponseDTO(CategorizationRules rules) {
        return new RuleResponseDTO(
                rules.getId(),
                rules.getKeyword(),
                rules.getCategory().getId(),
                rules.getCategory().getName(),
                rules.getPriority()
        );
    }
}
