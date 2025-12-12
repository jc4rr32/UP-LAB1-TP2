package exceptions;

/**
 * Excepción genérica para errores en la capa DAO. Capturan los errores de DAO.
 */
public class DAOException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
