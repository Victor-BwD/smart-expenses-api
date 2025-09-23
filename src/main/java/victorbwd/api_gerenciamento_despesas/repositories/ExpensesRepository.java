package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.dto.CategorySummaryDTO;
import victorbwd.api_gerenciamento_despesas.dto.ExpenseResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpensesRepository extends JpaRepository<Expenses, Long>, JpaSpecificationExecutor<Expenses> {

    Optional<Expenses> findByIdAndUserId(Long id, UUID userId);

    @Query("""
            SELECT new victorbwd.api_gerenciamento_despesas.dto.CategorySummaryDTO(
                        e.category.name,
                        SUM(e.amount),
                        COUNT(e)
                    )
                    FROM Expenses e
                    WHERE e.user.id = :userId
                    AND e.date BETWEEN :startDate AND :endDate
                    GROUP BY e.category.id, e.category.name
                    ORDER BY SUM(e.amount) DESC
            """)
    List<CategorySummaryDTO> getCategorySummary(@Param("userId") UUID userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}
