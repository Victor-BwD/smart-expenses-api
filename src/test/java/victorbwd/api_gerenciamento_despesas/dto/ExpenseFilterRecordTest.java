package victorbwd.api_gerenciamento_despesas.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseFilterRecordTest {

    private final Validator validator;

    ExpenseFilterRecordTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createCategoryDtoShouldHaveViolationsWhenInvalid() {
        CreateCategoryDTO dto = new CreateCategoryDTO("", "desc", "#FFF");

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void createCategoryDtoShouldPassWhenValid() {
        CreateCategoryDTO dto = new CreateCategoryDTO("Food", "desc", "#FFFFFF");

        assertTrue(validator.validate(dto).isEmpty());
    }
}

