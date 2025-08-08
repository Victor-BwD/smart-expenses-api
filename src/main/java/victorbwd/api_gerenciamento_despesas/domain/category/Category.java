package victorbwd.api_gerenciamento_despesas.domain.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    private String color; // Hexadecimal color code (e.g., "#FF5733")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expenses> expenses;

    public Category(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
}
