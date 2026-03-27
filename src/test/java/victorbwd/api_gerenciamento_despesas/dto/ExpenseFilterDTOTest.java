package victorbwd.api_gerenciamento_despesas.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpenseFilterDTOTest {

    @Test
    void shouldNormalizeNegativePageAndInvalidLimit() {
        ExpenseFilterDTO dto = new ExpenseFilterDTO(null, null, null, null, -5, 0);

        assertEquals(0, dto.page());
        assertEquals(10, dto.limit());
    }

    @Test
    void shouldCapLimitAt100() {
        ExpenseFilterDTO dto = new ExpenseFilterDTO(null, null, null, null, 1, 999);

        assertEquals(100, dto.limit());
    }
}

