package victorbwd.api_gerenciamento_despesas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import victorbwd.api_gerenciamento_despesas.domain.expenses.Expenses;
import victorbwd.api_gerenciamento_despesas.dto.CreateExpenseDTO;
import victorbwd.api_gerenciamento_despesas.dto.ExpenseResponseDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.ExpensesService;

import java.net.URI;
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
}
