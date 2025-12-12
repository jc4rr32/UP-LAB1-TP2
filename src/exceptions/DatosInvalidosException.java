package exceptions;

/**
 * Excepción lanzada cuando los datos proporcionados no cumplen la validación. No es un error del sistema, es un error del usuario.
 */
public class DatosInvalidosException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DatosInvalidosException(String message) {
        super(message);
    }

    public DatosInvalidosException(String message, Throwable cause) {
        super(message, cause);
    }
}