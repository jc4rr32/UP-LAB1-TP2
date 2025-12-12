package base;
/**
 * Representa un médico en el sistema. Extiende de Usuario y agrega el atributo
 * honorariosPorConsulta para reflejar cuánto cobra por cada consulta médica.
 */
public class Medico extends Usuario {
    private double honorariosPorConsulta;

    /**
     * Constructor completo, con ID y DNI conocidos (por ejemplo, al leer de la base de datos)
     */
    public Medico(int id, String dni, String nombre, String apellido, String email, double honorarios, ObraSocial obraSocial) {
        // Pasamos la obraSocial al padre (Usuario)
        super(id, dni, nombre, apellido, email, Rol.MEDICO, obraSocial);
        this.honorariosPorConsulta = honorarios;
    }

    /**
     * Constructor para nuevos médicos (ID asignado automáticamente al guardar)
     */
    public Medico(String dni, String nombre, String apellido, String email, double honorarios, ObraSocial obraSocial) {
        // Pasamos la obraSocial al padre
        super(dni, nombre, apellido, email, Rol.MEDICO, obraSocial);
        this.honorariosPorConsulta = honorarios;
    }

    public double getHonorariosPorConsulta() {
        return honorariosPorConsulta;
    }
    public void setHonorariosPorConsulta(double honorariosPorConsulta) {
        this.honorariosPorConsulta = honorariosPorConsulta;
    }

    @Override
    public String toString() {
    	// Agrego la obra social al toString para verlo en los logs o combos
        String os = (getObraSocial() != null) ? getObraSocial().name() : "Sin OS";
        return super.getNombre() + " " + super.getApellido() + " (" + os + ") - $" + honorariosPorConsulta;
    }
}