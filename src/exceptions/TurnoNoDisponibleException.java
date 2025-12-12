package exceptions;

//esta excepcion sirve para representar la ruptura de la regla de negocio de "no se puede dar turno porque ya está ocupado"

public class TurnoNoDisponibleException extends Exception {
    private static final long serialVersionUID = 1L;

    public TurnoNoDisponibleException(String message) {
        super(message);
    }
}