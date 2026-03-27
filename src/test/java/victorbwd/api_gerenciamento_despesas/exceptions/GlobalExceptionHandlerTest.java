package victorbwd.api_gerenciamento_despesas.exceptions;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleUserAlreadyExists() {
        Map<String, String> body = handler.handleUserExists(new UserAlreadyExistsException("User already exists"));

        assertEquals("User already exists", body.get("error"));
    }

    @Test
    void shouldHandleIllegalArgument() {
        Map<String, String> body = handler.handleIllegalArgumentException(new IllegalArgumentException("invalid input"));

        assertEquals("invalid input", body.get("error"));
    }

    @Test
    void shouldHandleCategoryNotFound() {
        Map<String, String> body = handler.handleCategoryNotFoundException(new CategoryNotFoundException("Category not found"));

        assertEquals("Category not found", body.get("error"));
    }

    @Test
    void shouldHandleUserNotFound() {
        Map<String, String> body = handler.handleUserNotFoundException(new UserNotFoundException("User not found"));

        assertEquals("User not found", body.get("error"));
    }

    @Test
    void shouldHandleExpenseNotFound() {
        Map<String, String> body = handler.handleExpenseNotFoundException(new ExpenseNotFoundException("Expense not found"));

        assertEquals("Expense not found", body.get("error"));
    }

    @Test
    void shouldHandleInvalidAuthentication() {
        Map<String, String> body = handler.handleInvalidAuthenticationException(new InvalidAuthenticationException("Authentication is not valid"));

        assertEquals("Authentication is not valid", body.get("error"));
    }

    @Test
    void shouldHandleGenericException() {
        Map<String, String> body = handler.handleGeneralException(new RuntimeException("boom"));

        assertEquals("An unexpected error occurred: boom", body.get("error"));
    }
}

