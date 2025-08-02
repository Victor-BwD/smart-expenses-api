package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.dto.ExpenseResponseDTO;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExpensesRepository extends JpaRepository<Expenses, Long>, JpaSpecificationExecutor<Expenses> {

    Optional<Expenses> findByIdAndUserId(Long id, UUID userId);
}
