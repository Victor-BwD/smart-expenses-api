package victorbwd.api_gerenciamento_despesas.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    @Test
    void getUserShouldReturnSuccessMessage() {
        UserController controller = new UserController();

        ResponseEntity<String> response = controller.getUser();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Sucesso", response.getBody());
    }
}

