package exceptions;

/**
 * Excepción lanzada por la capa de Servicio (Lógica de Negocio).
 * Se utiliza para envolver excepciones de capas inferiores (como DAOException)
 * o para indicar errores propios de la lógica de negocio.
 */
public class ServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}