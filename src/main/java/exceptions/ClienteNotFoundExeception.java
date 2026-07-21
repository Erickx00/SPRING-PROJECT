package exceptions;

public class ClienteNotFoundExeception extends RuntimeException {
    public ClienteNotFoundExeception(String message) {
        super(message);
    }
}
