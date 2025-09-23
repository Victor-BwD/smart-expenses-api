package victorbwd.api_gerenciamento_despesas.services;

import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.dto.CategorySummaryDTO;
import victorbwd.api_gerenciamento_despesas.repositories.ExpensesRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {
    private final ExpensesRepository expensesRepository;
    private final UserRepository userRepository;

    public ReportService(ExpensesRepository expensesRepository, UserRepository userRepository) {
        this.expensesRepository = expensesRepository;
        this.userRepository = userRepository;
    }

    public List<CategorySummaryDTO> getSummaryByCategory(UUID userId, LocalDate startDate, LocalDate endDate) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return expensesRepository.getCategorySummary(userId, startDate, endDate);
    }
}
