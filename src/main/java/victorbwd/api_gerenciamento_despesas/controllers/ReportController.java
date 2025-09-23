package victorbwd.api_gerenciamento_despesas.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import victorbwd.api_gerenciamento_despesas.dto.CategorySummaryDTO;
import victorbwd.api_gerenciamento_despesas.services.AuthService;
import victorbwd.api_gerenciamento_despesas.services.ExpensesService;
import victorbwd.api_gerenciamento_despesas.services.ReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportService reportService;
    private final AuthService authService;

    public ReportController(ReportService reportService, AuthService authService, ExpensesService expensesService) {
        this.reportService = reportService;
        this.authService = authService;
    }

    @GetMapping("/summary/by-category" )
    public ResponseEntity<List<CategorySummaryDTO>> getCategorySummary(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                       @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                       Authentication auth) {
        UUID userId = authService.extractUserIdFromAuth(auth);
        List<CategorySummaryDTO> summary = reportService.getSummaryByCategory(userId, startDate, endDate);

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/by-period" )
    public ResponseEntity<?> getPeriodSummary(@RequestParam("periodType") String periodType,
                                              @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                              @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                              Authentication auth) {

        UUID userId = authService.extractUserIdFromAuth(auth);

        var summary = reportService.getSummaryByPeriod(periodType, userId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
}
