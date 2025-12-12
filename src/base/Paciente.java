package base;
/**
 * Representa un paciente extendiendo de Usuario.
 * Actualmente no agrega atributos adicionales, pero se separa
 * como clase específica por si en el futuro se requieren 
 * campos o comportamientos particulares para pacientes.
 */
public class Paciente extends Usuario {

    /**
     * Constructor completo, con ID y DNI conocidos (por ejemplo, al leer de la base de datos)
     */
	public Paciente(int id, String dni, String nombre, String apellido, String email, ObraSocial obraSocial) {
        // Pasamos la obraSocial al padre
        super(id, dni, nombre, apellido, email, Rol.PACIENTE, obraSocial);
    }

    /**
     * Constructor para nuevos pacientes (ID asignado automáticamente al guardar)
     */
	public Paciente(String dni, String nombre, String apellido, String email, ObraSocial obraSocial) {
        // Pasamos la obraSocial al padre
        super(dni, nombre, apellido, email, Rol.PACIENTE, obraSocial);
    }

	@Override
    public String toString() {
        String os = (getObraSocial() != null) ? getObraSocial().name() : "Sin OS";
        return super.getNombre() + " " + super.getApellido() + " (OS: " + os + ")";
    }
}