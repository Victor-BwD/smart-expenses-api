package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;

public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
}
