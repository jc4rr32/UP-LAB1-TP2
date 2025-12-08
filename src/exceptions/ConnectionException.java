package exceptions;

/**
 * Excepción para errores en la conexión a la base de datos.
 */
public class ConnectionException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}