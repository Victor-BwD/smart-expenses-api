package victorbwd.api_gerenciamento_despesas.domain.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public Category(String name, String description, String color) {
        this.name = name;
        this.description = description;
        this.color = color;
    }
}
