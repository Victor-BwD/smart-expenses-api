package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.dto.ExpenseResponseDTO;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface ExpensesRepository extends JpaRepository<Expenses, Long> {

    @Query("SELECT e FROM Expenses e WHERE e.user.id = :userId " +
            "AND (CAST(:startDate AS date) IS NULL OR e.date >= :startDate) " +
            "AND (CAST(:endDate AS date) IS NULL OR e.date <= :endDate) " +
            "AND (CAST(:categoryId AS long) IS NULL OR e.category.id = :categoryId) " +
            "AND (CAST(:description AS string) IS NULL OR :description = '' OR LOWER(e.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    Page<Expenses> findExpensesWithFilters(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId,
            @Param("description") String description,
            Pageable pageable
            );

    Optional<Expenses> findByIdAndUserId(Long id, UUID userId);
}
