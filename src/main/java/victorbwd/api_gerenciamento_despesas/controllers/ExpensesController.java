package victorbwd.api_gerenciamento_despesas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.*;
import victorbwd.api_gerenciamento_despesas.exceptions.UserNotFoundException;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.ExpensesService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/expenses")
public class ExpensesController {
    private final ExpensesService expensesService;
    private final AuthService authService;

    public ExpensesController(ExpensesService expensesService, AuthService authService) {
        this.expensesService = expensesService;
        this.authService = authService;
    }

    @PostMapping("/create")
    public ResponseEntity<ExpenseResponseDTO> createExpense(@RequestBody CreateExpenseDTO dto, Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);
        Expenses expenses = expensesService.create(dto, userId);

        URI uri = URI.create("/expenses/" + expenses.getId());

        ExpenseResponseDTO responseDTO = new ExpenseResponseDTO(
               expenses.getId(),
                expenses.getDescription(),
                expenses.getAmount(),
                expenses.getCategory().getName(),
                expenses.getDate(),
                expenses.getUser().getName()
        );

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @GetMapping("/my-expenses")
    public ResponseEntity<List<ExpenseResponseDTO>> myExpenses(Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);

        User user = authService.getUserById(userId);

        List<ExpenseResponseDTO> expensesDTO = user.getExpenses().stream().map(expense -> new ExpenseResponseDTO(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory().getName(),
                expense.getDate(),
                expense.getUser().getName()
        )).toList();

        return ResponseEntity.ok(expensesDTO);
    }

    @GetMapping(value ="/expense/{id}")
    public ResponseEntity<ExpenseResponseDTO> getById(@PathVariable Integer id, Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);
        if (userId == null) {
            throw new UserNotFoundException("User not found");
        }
        User user = authService.getUserById(userId);

        ExpenseResponseDTO expense = expensesService.getById(id, user.getId());

        if (expense == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(expense);
    }

    @GetMapping
    public ResponseEntity<PagedExpenseResponseDTO> listExpenses(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit,
            Authentication auth) {

        UUID userId = authService.extractUserIdFromAuth(auth);
        if (userId == null) {
            throw new UserNotFoundException("User not found");
        }
        User user = authService.getUserById(userId);

        ExpenseFilterDTO filters = new ExpenseFilterDTO(
                startDate,
                endDate,
                categoryId,
                description,
                page,
                limit
        );

        PagedExpenseResponseDTO response = expensesService.listExpenses(filters, user.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponseDTO> updateExpense(
            @PathVariable Integer id,
            @RequestBody UpdateExpenseDTO dto,
            Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);
        if (userId == null) {
            throw new UserNotFoundException("User not found");
        }
        User user = authService.getUserById(userId);

        ExpenseResponseDTO updatedExpense = expensesService.update(id, dto, user.getId());

        return ResponseEntity.ok(updatedExpense);
    }
}
