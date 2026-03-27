package victorbwd.api_gerenciamento_despesas.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UpdateExpenseDTOTest {

    @Test
    void shouldCreateRecordWhenValid() {
        UpdateExpenseDTO dto = new UpdateExpenseDTO("Internet", 120.0, "Bills", LocalDate.of(2026, 3, 1));

        assertEquals("Internet", dto.description());
        assertEquals(120.0, dto.value());
    }

    @Test
    void shouldThrowWhenDescriptionIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new UpdateExpenseDTO(" ", 100.0, "Bills", LocalDate.now()));
    }

    @Test
    void shouldThrowWhenValueIsZeroOrNegative() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new UpdateExpenseDTO("Desc", 0.0, "Bills", LocalDate.now())),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new UpdateExpenseDTO("Desc", -1.0, "Bills", LocalDate.now()))
        );
    }

    @Test
    void expenseFilterShouldNormalizePageAndLimit() {
        ExpenseFilterDTO dtoWithInvalid = new ExpenseFilterDTO(null, null, null, null, -2, 0);
        ExpenseFilterDTO dtoWithTooHighLimit = new ExpenseFilterDTO(null, null, null, null, 0, 999);

        assertEquals(0, dtoWithInvalid.page());
        assertEquals(10, dtoWithInvalid.limit());
        assertEquals(100, dtoWithTooHighLimit.limit());
    }
}
