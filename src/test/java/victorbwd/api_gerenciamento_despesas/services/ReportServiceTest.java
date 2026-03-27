package victorbwd.api_gerenciamento_despesas.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import victorbwd.api_gerenciamento_despesas.dto.CategorySummaryDTO;
import victorbwd.api_gerenciamento_despesas.dto.PeriodSummaryDTO;
import victorbwd.api_gerenciamento_despesas.repositories.ExpensesRepository;
import victorbwd.api_gerenciamento_despesas.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ExpensesRepository expensesRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReportService reportService;

    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    @Test
    void getSummaryByCategoryShouldThrowWhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> reportService.getSummaryByCategory(userId, LocalDate.now().minusDays(10), LocalDate.now()));
    }

    @Test
    void getSummaryByCategoryShouldReturnRepositoryData() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new victorbwd.api_gerenciamento_despesas.domain.user.User()));
        when(expensesRepository.getCategorySummary(userId, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31)))
                .thenReturn(List.of(new CategorySummaryDTO("Food", BigDecimal.valueOf(123.45), 3L)));

        List<CategorySummaryDTO> result = reportService.getSummaryByCategory(userId,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertEquals(1, result.size());
        assertEquals("Food", result.get(0).categoryName());
    }

    @Test
    void getSummaryByPeriodShouldMapMonthlyResults() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new victorbwd.api_gerenciamento_despesas.domain.user.User()));
        when(expensesRepository.getMonthlySummariesRaw(userId, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 28)))
                .thenReturn(List.<Object[]>of(new Object[]{2026, 2, BigDecimal.valueOf(450), 4L}));

        List<PeriodSummaryDTO> result = reportService.getSummaryByPeriod("monthly", userId,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 28));

        assertEquals(1, result.size());
        assertEquals("2026-02", result.get(0).period());
        assertEquals(4L, result.get(0).transactionCount());
    }

    @Test
    void getSummaryByPeriodShouldMapYearlyResults() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new victorbwd.api_gerenciamento_despesas.domain.user.User()));
        when(expensesRepository.getYearlySummariesRaw(userId, LocalDate.of(2024, 1, 1), LocalDate.of(2026, 12, 31)))
                .thenReturn(List.<Object[]>of(new Object[]{2026, BigDecimal.valueOf(1000), 10L}));

        List<PeriodSummaryDTO> result = reportService.getSummaryByPeriod("yearly", userId,
                LocalDate.of(2024, 1, 1), LocalDate.of(2026, 12, 31));

        assertEquals(1, result.size());
        assertEquals("2026", result.get(0).period());
    }

    @Test
    void getSummaryByPeriodShouldThrowForInvalidType() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(new victorbwd.api_gerenciamento_despesas.domain.user.User()));

        assertThrows(IllegalArgumentException.class,
                () -> reportService.getSummaryByPeriod("weekly", userId, LocalDate.now().minusDays(10), LocalDate.now()));
    }
}


