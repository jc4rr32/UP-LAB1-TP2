package base;
/**
 * Representa un usuario administrador.
 */
public class Administrador extends Usuario {

    /**
     * Constructor completo, con ID y DNI conocidos.
     */
    public Administrador(int id, String dni, String nombre, String apellido, String email) {
        super(id, dni, nombre, apellido, email, Rol.ADMIN);
    }

    /**
     * Constructor para nuevos admins (ID asignado al guardar).
     */
    public Administrador(String dni, String nombre, String apellido, String email) {
        super(dni, nombre, apellido, email, Rol.ADMIN);
    }
}