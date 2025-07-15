package victorbwd.api_gerenciamento_despesas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.domain.user.User;
import victorbwd.api_gerenciamento_despesas.dto.CreateExpenseDTO;
import victorbwd.api_gerenciamento_despesas.dto.ExpenseResponseDTO;
import victorbwd.api_gerenciamento_despesas.exceptions.UserNotFoundException;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.ExpensesService;

import java.net.URI;
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
}
