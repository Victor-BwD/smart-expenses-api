package victorbwd.api_gerenciamento_despesas.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.CreateExpenseDTO;
import victorbwd.api_gerenciamento_despesas.repositories.CategoryRepository;
import victorbwd.api_gerenciamento_despesas.repositories.ExpensesRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ExpensesService {
    private final ExpensesRepository expensesRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;


    public ExpensesService(ExpensesRepository expensesRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository) {
        this.expensesRepository = expensesRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Expenses create(CreateExpenseDTO dto, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findByName(dto.category()).orElseThrow(() -> new RuntimeException("Category not found"));

        Expenses expenses = new Expenses();

        expenses.setAmount(BigDecimal.valueOf(dto.value()));
        expenses.setDescription(dto.description());
        expenses.setUser(user);
        expenses.setCategory(category);
        expenses.setDate(LocalDate.now());
        expenses.setCreatedAt(LocalDate.now().atStartOfDay());
        expenses.setUpdatedAt(LocalDate.now().atStartOfDay());

        return expensesRepository.save(expenses);
    }
}
