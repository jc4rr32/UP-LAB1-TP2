package base;
/**
 * Representa un usuario administrador. 
 * TODO: manejar el ABM desde el sistema y no desde la DB.
 */
public class Administrador extends Usuario {

    /**
     * Constructor completo, con ID y DNI conocidos.
     */
	public Administrador(int id, String dni, String nombre, String apellido, String email) {
        // Al admin le pasamos null en Obra Social porque no la usa para el negocio
        super(id, dni, nombre, apellido, email, Rol.ADMIN, null);
    }

    /**
     * Constructor para nuevos admins (ID asignado al guardar).
     */
	public Administrador(String dni, String nombre, String apellido, String email) {
        super(dni, nombre, apellido, email, Rol.ADMIN, null);
    }
}