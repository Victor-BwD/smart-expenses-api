package victorbwd.api_gerenciamento_despesas.repositories.specifications;

import org.springframework.data.jpa.domain.Specification;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;

import java.time.LocalDate;
import java.util.UUID;

public class ExpenseSpecifications {
    public static Specification<Expenses> withUserId(UUID userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Expenses> withStartDate(LocalDate startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("date"), startDate);
    }

    public static Specification<Expenses> withEndDate(LocalDate endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("date"), endDate);
    }

    public static Specification<Expenses> withCategoryId(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Expenses> withDescription(String description) {
        return (root, query, cb) -> {
            if (description == null || description.trim().isEmpty()) {
                return null;
            }
            return cb.like(
                    cb.lower(root.get("description")),
                    "%" + description.toLowerCase() + "%"
            );
        };
    }
}
