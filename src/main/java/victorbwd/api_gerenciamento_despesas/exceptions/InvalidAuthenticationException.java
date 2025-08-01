package victorbwd.api_gerenciamento_despesas.exceptions;

public class InvalidAuthenticationException extends RuntimeException {
    public InvalidAuthenticationException(String message) {
        super(message);
    }
}
