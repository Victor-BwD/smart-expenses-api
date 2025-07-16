package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;

import java.time.LocalDate;
import java.util.UUID;

public interface ExpensesRepository extends JpaRepository<Expenses, Long> {

    @Query("SELECT e FROM Expenses e WHERE e.user.id = :userId " +
            "AND (:startDate IS NULL OR e.date >= :startDate) " +
            "AND (:endDate IS NULL OR e.date <= :endDate) " +
            "AND (:categoryId IS NULL OR e.category.id = :categoryId) " +
            "AND (:description IS NULL OR UPPER(e.description) LIKE UPPER(CONCAT('%', :description, '%')))")
    Page<Expenses> findExpensesWithFilters(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("categoryId") Long categoryId,
            @Param("description") String description,
            Pageable pageable
            );
}
