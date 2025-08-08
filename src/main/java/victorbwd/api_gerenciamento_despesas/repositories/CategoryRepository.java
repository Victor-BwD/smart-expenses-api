package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    @Query("SELECT ct FROM Category ct WHERE ct.user.id = ?1 OR ct.isDefault = true")
    List<Category> findByUserIdOrDefault(UUID userId);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.name = :name AND (c.user.id = :userId OR c.user IS NULL)")
    boolean existsByNameAndUserIdOrDefault(@Param("name") String name, @Param("userId") UUID userId);

    Optional<Category> findByName(String name);
}
