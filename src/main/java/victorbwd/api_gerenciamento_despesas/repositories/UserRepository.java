package victorbwd.api_gerenciamento_despesas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import victorbwd.api_gerenciamento_despesas.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
}
