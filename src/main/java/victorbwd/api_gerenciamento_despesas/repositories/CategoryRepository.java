package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    @Query("SELECT ct FROM Category ct WHERE ct.user.id = ?1 OR ct.isDefault = true")
    List<Category> findByUserIdOrDefault(UUID userId);

    boolean existsByName(String name);

    Optional<Category> findByName(String name);
}
