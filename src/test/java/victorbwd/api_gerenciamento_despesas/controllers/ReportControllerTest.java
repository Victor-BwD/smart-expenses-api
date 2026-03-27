package victorbwd.api_gerenciamento_despesas.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import victorbwd.api_gerenciamento_despesas.dto.CategorySummaryDTO;
import victorbwd.api_gerenciamento_despesas.dto.PeriodSummaryDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.ExpensesService;
import victorbwd.api_gerenciamento_despesas.services.ReportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    private ReportService reportService;
    private AuthService authService;
    private ReportController controller;
    private Authentication authentication;
    private UUID userId;

    @BeforeEach
    void setup() {
        reportService = mock(ReportService.class);
        authService = mock(AuthService.class);
        controller = new ReportController(reportService, authService, mock(ExpensesService.class));
        authentication = mock(Authentication.class);
        userId = UUID.randomUUID();

        when(authService.extractUserIdFromAuth(authentication)).thenReturn(userId);
    }

    @Test
    void getCategorySummaryShouldReturnOk() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        when(reportService.getSummaryByCategory(userId, start, end))
                .thenReturn(List.of(new CategorySummaryDTO("Food", BigDecimal.TEN, 2L)));

        ResponseEntity<List<CategorySummaryDTO>> response = controller.getCategorySummary(start, end, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Food", response.getBody().get(0).categoryName());
    }

    @Test
    void getPeriodSummaryShouldReturnOk() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        when(reportService.getSummaryByPeriod("monthly", userId, start, end))
                .thenReturn(List.of(new PeriodSummaryDTO("2026-01", BigDecimal.valueOf(100), 1L)));

        ResponseEntity<?> response = controller.getPeriodSummary("monthly", start, end, authentication);

        assertEquals(200, response.getStatusCode().value());
    }
}

