package victorbwd.api_gerenciamento_despesas.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.*;
import victorbwd.api_gerenciamento_despesas.exceptions.CategoryNotFoundException;
import victorbwd.api_gerenciamento_despesas.exceptions.ExpenseNotFoundException;
import victorbwd.api_gerenciamento_despesas.exceptions.UserNotFoundException;
import victorbwd.api_gerenciamento_despesas.repositories.CategoryRepository;
import victorbwd.api_gerenciamento_despesas.repositories.ExpensesRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpensesServiceTest {

    @Mock
    private ExpensesRepository expensesRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategorizationEngineService categorizationEngineService;

    @InjectMocks
    private ExpensesService expensesService;

    private UUID userId;
    private User user;
    private Category food;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setName("Victor");
        user.setEmail("victor@mail.com");

        food = new Category();
        food.setId(10);
        food.setName("Food");
    }

    @Test
    void createShouldThrowWhenUserDoesNotExist() {
        CreateExpenseDTO dto = new CreateExpenseDTO("Lunch", 50.0, "Food", null, LocalDate.now());
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> expensesService.create(dto, userId));
    }

    @Test
    void createShouldUseProvidedCategoryWhenPresent() {
        CreateExpenseDTO dto = new CreateExpenseDTO("Lunch", 50.0, "Food", null, LocalDate.of(2026, 1, 10));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Food")).thenReturn(Optional.of(food));
        when(expensesRepository.save(any(Expenses.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expenses created = expensesService.create(dto, userId);

        assertEquals(food, created.getCategory());
        assertEquals(BigDecimal.valueOf(50.0), created.getAmount());
        assertEquals(LocalDate.of(2026, 1, 10), created.getDate());
        verify(categorizationEngineService, never()).findCategoryForRule(any());
    }

    @Test
    void createShouldAutoCategorizeWhenCategoryNotProvided() {
        CreateExpenseDTO dto = new CreateExpenseDTO("Uber ride", 23.5, null, null, LocalDate.now());
        Category transport = new Category();
        transport.setId(20);
        transport.setName("Transport");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categorizationEngineService.findCategoryForRule(any(Expenses.class))).thenReturn(Optional.of(transport));
        when(expensesRepository.save(any(Expenses.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expenses created = expensesService.create(dto, userId);

        assertEquals("Transport", created.getCategory().getName());
        verify(categoryRepository, never()).findByName("Sem Categoria");
    }

    @Test
    void createShouldUseDefaultCategoryWhenNoRuleMatches() {
        CreateExpenseDTO dto = new CreateExpenseDTO("Random expense", 10.0, "", null, null);
        Category defaultCategory = new Category();
        defaultCategory.setId(99);
        defaultCategory.setName("Sem Categoria");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categorizationEngineService.findCategoryForRule(any(Expenses.class))).thenReturn(Optional.empty());
        when(categoryRepository.findByName("Sem Categoria")).thenReturn(Optional.of(defaultCategory));
        when(expensesRepository.save(any(Expenses.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expenses created = expensesService.create(dto, userId);

        assertEquals("Sem Categoria", created.getCategory().getName());
    }

    @Test
    void createShouldThrowWhenProvidedCategoryDoesNotExist() {
        CreateExpenseDTO dto = new CreateExpenseDTO("Lunch", 50.0, "Food", null, LocalDate.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("Food")).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> expensesService.create(dto, userId));
    }

    @Test
    void listExpensesShouldReturnPagedResponse() {
        ExpenseFilterDTO filter = new ExpenseFilterDTO(LocalDate.now().minusDays(30), LocalDate.now(), null, "uber", 0, 10);

        Expenses exp = new Expenses();
        exp.setId(1L);
        exp.setUser(user);
        exp.setCategory(food);
        exp.setDescription("Uber");
        exp.setAmount(BigDecimal.valueOf(42));
        exp.setDate(LocalDate.of(2026, 2, 2));

        Page<Expenses> page = new PageImpl<>(List.of(exp));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(expensesRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        PagedExpenseResponseDTO response = expensesService.listExpenses(filter, userId);

        assertEquals(1, response.expenses().size());
        assertEquals("Uber", response.expenses().get(0).description());
        assertEquals(1L, response.totalElements());
    }

    @Test
    void getByIdShouldReturnExpenseResponse() {
        Expenses exp = new Expenses();
        exp.setId(5L);
        exp.setUser(user);
        exp.setCategory(food);
        exp.setDescription("Market");
        exp.setAmount(BigDecimal.TEN);
        exp.setDate(LocalDate.of(2026, 1, 1));

        when(expensesRepository.findByIdAndUserId(5L, userId)).thenReturn(Optional.of(exp));

        ExpenseResponseDTO response = expensesService.getById(5L, userId);

        assertEquals("Market", response.description());
        assertEquals("Food", response.category());
    }

    @Test
    void getByIdShouldThrowWhenExpenseNotFound() {
        when(expensesRepository.findByIdAndUserId(5L, userId)).thenReturn(Optional.empty());

        assertThrows(ExpenseNotFoundException.class, () -> expensesService.getById(5L, userId));
    }

    @Test
    void updateShouldApplyCategoryWhenCategoryProvided() {
        Expenses exp = new Expenses();
        exp.setId(7L);
        exp.setUser(user);
        exp.setCategory(food);
        exp.setDate(LocalDate.of(2026, 1, 1));

        Category transport = new Category();
        transport.setId(21);
        transport.setName("Transport");

        UpdateExpenseDTO dto = new UpdateExpenseDTO("Bus", 12.0, "Transport", LocalDate.of(2026, 1, 8));

        when(expensesRepository.findByIdAndUserId(7L, userId)).thenReturn(Optional.of(exp));
        when(categoryRepository.findByName("Transport")).thenReturn(Optional.of(transport));
        when(expensesRepository.save(any(Expenses.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponseDTO response = expensesService.update(7L, dto, userId);

        assertEquals("Transport", response.category());
        assertEquals("Bus", response.description());
        verify(categorizationEngineService, never()).findCategoryForRule(any());
    }

    @Test
    void updateShouldTryAutoCategorizationWhenCategoryNotProvided() {
        Expenses exp = new Expenses();
        exp.setId(8L);
        exp.setUser(user);
        exp.setCategory(food);

        UpdateExpenseDTO dto = new UpdateExpenseDTO("Movie", 30.0, "", LocalDate.now());

        when(expensesRepository.findByIdAndUserId(8L, userId)).thenReturn(Optional.of(exp));
        when(categorizationEngineService.findCategoryForRule(any(Expenses.class))).thenReturn(Optional.empty());
        when(expensesRepository.save(any(Expenses.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponseDTO response = expensesService.update(8L, dto, userId);

        assertNull(response.category());
        verify(categorizationEngineService).findCategoryForRule(any(Expenses.class));
    }

    @Test
    void deleteShouldRemoveExpenseWhenFound() {
        Expenses exp = new Expenses();
        exp.setId(22L);
        when(expensesRepository.findByIdAndUserId(22L, userId)).thenReturn(Optional.of(exp));

        expensesService.delete(22L, userId);

        verify(expensesRepository).delete(exp);
    }

    @Test
    void recategorizeShouldThrowWhenExpenseIdsDoNotBelongToUser() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());

        Expenses exp = new Expenses();
        exp.setId(1L);
        exp.setUser(otherUser);

        RecategorizeRequestDTO dto = new RecategorizeRequestDTO(List.of(1L), null);
        when(expensesRepository.findAllById(dto.expenseIds())).thenReturn(List.of(exp));

        assertThrows(ExpenseNotFoundException.class, () -> expensesService.recategorizeExpenses(dto, userId));
    }

    @Test
    void recategorizeShouldSetSpecificCategoryWhenCategoryIdProvided() {
        Expenses exp1 = new Expenses();
        exp1.setId(1L);
        exp1.setUser(user);

        Expenses exp2 = new Expenses();
        exp2.setId(2L);
        exp2.setUser(user);

        Category target = new Category();
        target.setId(35);
        target.setName("Bills");

        RecategorizeRequestDTO dto = new RecategorizeRequestDTO(List.of(1L, 2L), 35L);

        when(expensesRepository.findAllById(dto.expenseIds())).thenReturn(List.of(exp1, exp2));
        when(categoryRepository.findByIdForUserOrDefault(35, userId)).thenReturn(Optional.of(target));
        when(expensesRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Expenses> result = expensesService.recategorizeExpenses(dto, userId);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> "Bills".equals(e.getCategory().getName())));
    }

    @Test
    void recategorizeShouldUseRulesWhenCategoryIdIsNull() {
        Expenses exp = new Expenses();
        exp.setId(1L);
        exp.setUser(user);
        exp.setDescription("ifood order");

        RecategorizeRequestDTO dto = new RecategorizeRequestDTO(List.of(1L), null);

        when(expensesRepository.findAllById(dto.expenseIds())).thenReturn(List.of(exp));
        when(categorizationEngineService.findCategoryForRule(exp)).thenReturn(Optional.of(food));
        when(expensesRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Expenses> result = expensesService.recategorizeExpenses(dto, userId);

        assertEquals("Food", result.get(0).getCategory().getName());
    }
}

