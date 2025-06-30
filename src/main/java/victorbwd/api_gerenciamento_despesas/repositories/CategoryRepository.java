package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;

public interface CategoryRepository extends JpaRepository<Category,Integer> {
    boolean existsByName(String name);
}
