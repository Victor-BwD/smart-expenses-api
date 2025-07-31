package victorbwd.api_gerenciamento_despesas.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.*;
import victorbwd.api_gerenciamento_despesas.exceptions.ExpenseNotFoundException;
import victorbwd.api_gerenciamento_despesas.repositories.CategoryRepository;
import victorbwd.api_gerenciamento_despesas.repositories.ExpensesRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.List;

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
        expenses.setDate(dto.date() != null ? dto.date() : LocalDate.now());
        expenses.setCreatedAt(LocalDate.now().atStartOfDay());
        expenses.setUpdatedAt(LocalDate.now().atStartOfDay());

        return expensesRepository.save(expenses);
    }

    public PagedExpenseResponseDTO listExpenses(ExpenseFilterDTO filters, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(
                filters.page(),
                filters.limit(),
                Sort.by(Sort.Direction.DESC, "date", "createdAt")
        );

        Page<Expenses> expensePage = expensesRepository.findExpensesWithFilters(
                user.getId(),
                filters.startDate(),
                filters.endDate(),
                filters.categoryId(),
                filters.description(),
                pageable
        );

        List<ExpenseResponseDTO> expensesDTOs = expensePage.getContent().stream().map(this::convertToResponseDTO).toList();

        return new PagedExpenseResponseDTO(
                expensesDTOs,
                expensePage.getTotalElements(),
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.hasNext(),
                expensePage.hasPrevious()
        );

    }

    public ExpenseResponseDTO getById(Integer id, UUID userId) {
        Expenses expense = expensesRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        return convertToResponseDTO(expense);
    }

    public ExpenseResponseDTO update(Integer id, UpdateExpenseDTO dto, UUID userId) {
        Expenses expense = expensesRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        Category category = categoryRepository.findByName(dto.category()).orElseThrow(() -> new RuntimeException("Category not found"));

        expense.setDescription(dto.description());
        expense.setAmount(BigDecimal.valueOf(dto.value()));
        expense.setCategory(category);
        expense.setDate(dto.date());
        expense.setUpdatedAt(LocalDate.now().atStartOfDay());
        Expenses updatedExpense = expensesRepository.save(expense);

        return convertToResponseDTO(updatedExpense);
    }

    public void delete(Integer id, UUID userId) {
        Expenses expense = expensesRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        expensesRepository.delete(expense);
    }

    private ExpenseResponseDTO convertToResponseDTO(Expenses expense) {
        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory().getName(),
                expense.getDate(),
                expense.getUser().getName()
        );
    }
}
