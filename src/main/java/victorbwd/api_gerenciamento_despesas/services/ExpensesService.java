package victorbwd.api_gerenciamento_despesas.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.*;
import victorbwd.api_gerenciamento_despesas.exceptions.CategoryNotFoundException;
import victorbwd.api_gerenciamento_despesas.exceptions.ExpenseNotFoundException;
import victorbwd.api_gerenciamento_despesas.exceptions.UserNotFoundException;
import victorbwd.api_gerenciamento_despesas.repositories.CategoryRepository;
import victorbwd.api_gerenciamento_despesas.repositories.specifications.ExpenseSpecifications;
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
    private final CategorizationEngineService categorizationEngineService;


    public ExpensesService(ExpensesRepository expensesRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository, CategorizationEngineService categorizationEngineService) {
        this.categorizationEngineService = categorizationEngineService;
        this.expensesRepository = expensesRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Expenses create(CreateExpenseDTO dto, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));



        Expenses expenses = new Expenses();

        expenses.setAmount(BigDecimal.valueOf(dto.value()));
        expenses.setDescription(dto.description());
        expenses.setUser(user);
        expenses.setDate(dto.date() != null ? dto.date() : LocalDate.now());
        expenses.setCreatedAt(LocalDate.now().atStartOfDay());
        expenses.setUpdatedAt(LocalDate.now().atStartOfDay());

        if(dto.category() != null && !dto.category().isBlank()) {
            Category category = categoryRepository.findByName(dto.category())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            expenses.setCategory(category);
        } else {
            categorizationEngineService.findCategoryForRule(expenses).ifPresent(expenses::setCategory);
        }

        return expensesRepository.save(expenses);
    }

    public PagedExpenseResponseDTO listExpenses(ExpenseFilterDTO filters, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(
                filters.page(),
                filters.limit(),
                Sort.by(Sort.Direction.DESC, "date", "createdAt")
        );

        Specification<Expenses> spec = Specification.allOf(ExpenseSpecifications.withUserId(userId))
                .and(ExpenseSpecifications.withStartDate(filters.startDate()))
                .and(ExpenseSpecifications.withEndDate(filters.endDate()))
                .and(ExpenseSpecifications.withCategoryId(filters.categoryId()))
                .and(ExpenseSpecifications.withDescription(filters.description()));

        Page<Expenses> expensePage = expensesRepository.findAll(spec, pageable);

        List<ExpenseResponseDTO> expensesDTOs = expensePage.getContent()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();

        return new PagedExpenseResponseDTO(
                expensesDTOs,
                expensePage.getTotalElements(),
                expensePage.getNumber(),
                expensePage.getSize(),
                expensePage.hasNext(),
                expensePage.hasPrevious()
        );

    }

    public ExpenseResponseDTO getById(Long id, UUID userId) {
        Expenses expense = expensesRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        return convertToResponseDTO(expense);
    }

    public ExpenseResponseDTO update(Long id, UpdateExpenseDTO dto, UUID userId) {
        Expenses expense = expensesRepository.findByIdAndUserId(id, userId).orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));



        expense.setDescription(dto.description());
        expense.setAmount(BigDecimal.valueOf(dto.value()));
        expense.setDate(dto.date());
        expense.setUpdatedAt(LocalDate.now().atStartOfDay());

        if (dto.category() != null && !dto.category().isBlank()) {
            Category category = categoryRepository.findByName(dto.category())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            expense.setCategory(category);
        }
        else {
            expense.setCategory(null);

            categorizationEngineService.findCategoryForRule(expense)
                    .ifPresent(expense::setCategory);
        }

        Expenses updatedExpense = expensesRepository.save(expense);

        return convertToResponseDTO(updatedExpense);
    }

    public void delete(Long id, UUID userId) {
        Expenses expense = expensesRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        expensesRepository.delete(expense);
    }

    private ExpenseResponseDTO convertToResponseDTO(Expenses expense) {
        String categoryName = expense.getCategory() != null ? expense.getCategory().getName() : null;

        return new ExpenseResponseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                categoryName,
                expense.getDate(),
                expense.getUser().getName()
        );
    }

    public List<Expenses> recategorizeExpenses(RecategorizeRequestDTO dto, UUID userId) {
        List<Expenses> expenses = expensesRepository.findAllById(dto.expenseIds());

        boolean hasInvalidExpenses = expenses.size() != dto.expenseIds().size() ||
                !expenses.stream().allMatch(exp -> exp.getUser().getId().equals(userId));

        if(hasInvalidExpenses){
            throw new ExpenseNotFoundException("One or more expenses not found for the user");
        }

        if (dto.categoryId() != null) {
            Category targetCategory = categoryRepository.findByIdForUserOrDefault(dto.categoryId().intValue(), userId).orElseThrow(
                    () -> new CategoryNotFoundException("Category not found")
            );

            expenses.forEach(expense -> {
                expense.setCategory(targetCategory);
            });
        }else{
            expenses.forEach(expense -> {
                expense.setCategory(null);
                categorizationEngineService.findCategoryForRule(expense).ifPresent(expense::setCategory);
            });
        }

        return expensesRepository.saveAll(expenses);

    }
}
