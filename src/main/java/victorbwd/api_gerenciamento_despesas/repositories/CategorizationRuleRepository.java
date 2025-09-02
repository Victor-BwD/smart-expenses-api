package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import victorbwd.api_gerenciamento_despesas.domain.rules.CategorizationRules;
import victorbwd.api_gerenciamento_despesas.domain.user.User;

import java.util.List;

public interface CategorizationRuleRepository extends JpaRepository<CategorizationRules, Long> {

    List<CategorizationRules> findByUserOrderByPriorityAsc(User user);
}
