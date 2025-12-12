package exceptions;

public class TurnoNoDisponibleException extends Exception {
    private static final long serialVersionUID = 1L;

    public TurnoNoDisponibleException(String message) {
        super(message);
    }
}