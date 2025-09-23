package victorbwd.api_gerenciamento_despesas.services;

import org.springframework.stereotype.Service;
import victorbwd.api_gerenciamento_despesas.dto.CategorySummaryDTO;
import victorbwd.api_gerenciamento_despesas.dto.PeriodSummaryDTO;
import victorbwd.api_gerenciamento_despesas.repositories.ExpensesRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.math.BigDecimal;
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

    public List<PeriodSummaryDTO> getSummaryByPeriod(String periodType, UUID userId, LocalDate startDate, LocalDate endDate) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        switch (periodType.toLowerCase()) {
            case "monthly":
                List<Object[]> monthlyResults = expensesRepository.getMonthlySummariesRaw(userId, startDate, endDate);
                return monthlyResults.stream()
                        .map(result -> {
                            Integer year = (Integer) result[0];
                            Integer month = (Integer) result[1];
                            BigDecimal total = (BigDecimal) result[2];
                            Long count = (Long) result[3];

                            String period = String.format("%d-%02d", year, month);

                            return new PeriodSummaryDTO(period, total, count);
                        })
                        .toList();
            case "yearly":
                List<Object[]> yearlyResults = expensesRepository.getYearlySummariesRaw(userId, startDate, endDate);
                return yearlyResults.stream()
                        .map(result -> new PeriodSummaryDTO(
                                String.valueOf(result[0]),
                                (BigDecimal) result[1],
                                (Long) result[2]
                        ))
                        .toList();
            default:
                throw new IllegalArgumentException("Invalid period type. Must be 'monthly' or 'yearly'.");
        }
    }
}
