package victorbwd.api_gerenciamento_despesas.domain.rules;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.user.User;

@Entity
@Table(name = "categorization_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorizationRules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer priority;
}
