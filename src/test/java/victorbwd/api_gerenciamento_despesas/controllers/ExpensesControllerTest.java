package victorbwd.api_gerenciamento_despesas.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import victorbwd.api_gerenciamento_despesas.domain.category.Category;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.*;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.ExpensesService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ExpensesControllerTest {

    private ExpensesService expensesService;
    private AuthService authService;
    private ExpensesController controller;
    private Authentication authentication;
    private UUID userId;
    private User user;

    @BeforeEach
    void setup() {
        expensesService = mock(ExpensesService.class);
        authService = mock(AuthService.class);
        controller = new ExpensesController(expensesService, authService);
        authentication = mock(Authentication.class);

        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setName("Victor");

        when(authService.extractUserIdFromAuth(authentication)).thenReturn(userId);
        when(authService.getUserById(userId)).thenReturn(user);
    }

    @Test
    void createExpenseShouldReturnCreated() {
        CreateExpenseDTO dto = new CreateExpenseDTO("Lunch", 20.0, "Food", null, LocalDate.of(2026, 3, 1));

        Category category = new Category();
        category.setName("Food");

        Expenses created = new Expenses();
        created.setId(10L);
        created.setDescription("Lunch");
        created.setAmount(BigDecimal.valueOf(20));
        created.setCategory(category);
        created.setDate(LocalDate.of(2026, 3, 1));
        created.setUser(user);

        when(expensesService.create(dto, userId)).thenReturn(created);

        ResponseEntity<ExpenseResponseDTO> response = controller.createExpense(dto, authentication);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("Lunch", response.getBody().description());
    }

    @Test
    void listExpensesShouldReturnOk() {
        PagedExpenseResponseDTO paged = new PagedExpenseResponseDTO(List.of(), 0, 0, 10, false, false);
        when(expensesService.listExpenses(any(ExpenseFilterDTO.class), eq(userId))).thenReturn(paged);

        ResponseEntity<PagedExpenseResponseDTO> response = controller.listExpenses(null, null, null, null, 0, 10, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(0, response.getBody().totalElements());
    }

    @Test
    void deleteExpenseShouldReturnNoContent() {
        ResponseEntity<Void> response = controller.deleteExpense(1L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(expensesService).delete(1L, userId);
    }

    @Test
    void recategorizeShouldReturnMappedExpenses() {
        Category category = new Category();
        category.setName("Food");

        Expenses expense = new Expenses();
        expense.setId(1L);
        expense.setDescription("Lunch");
        expense.setAmount(BigDecimal.valueOf(10));
        expense.setCategory(category);
        expense.setDate(LocalDate.now());
        expense.setUser(user);

        RecategorizeRequestDTO dto = new RecategorizeRequestDTO(List.of(1L), null);
        when(expensesService.recategorizeExpenses(dto, userId)).thenReturn(List.of(expense));

        ResponseEntity<List<ExpenseResponseDTO>> response = controller.recategorize(dto, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Food", response.getBody().get(0).category());
    }
}

