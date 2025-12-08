package base;


import java.time.LocalDateTime;

/**
 * Representa un turno médico, incluyendo fecha y hora, médico asignado y paciente.
 * Asegura que un médico y un paciente se encuentren en una cita específica.
 */
public class Turno {

    private int id;
    private LocalDateTime fechaHora;
    private Medico medico;
    private Paciente paciente;
    // Si no es SobreTurno, queda null
    private Integer turnoPadreId;  

    public Turno(int id, LocalDateTime fechaHora, Medico medico, Paciente paciente, Integer turnoPadreId) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.medico = medico;
        this.paciente = paciente;
        this.turnoPadreId = turnoPadreId;
    }

    // Constructor para nuevos turnos (por ejemplo, cuando aún no se insertó en la db)
    public Turno(LocalDateTime fechaHora, Medico medico, Paciente paciente) {
    	// -1 indica nuevo turno
        this(-1, fechaHora, medico, paciente, null);
    }
    
    // Constructor para sobreturno 
    public Turno(LocalDateTime fechaHora, Medico medico, Paciente paciente, int turnoPadreId) {
        this(-1, fechaHora, medico, paciente, turnoPadreId);
    }
    
    public int getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Medico getMedico() {
        return medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }
    
    public double getCosto() {
        return medico.getHonorariosPorConsulta();
    }

    public Integer getTurnoPadreId() {
        return turnoPadreId;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    
    public void setTurnoPadreId(Integer turnoPadreId) {
        this.turnoPadreId = turnoPadreId;
    }
    
    /** Devuelve true si este turno es un sobreturno. */
    public boolean isSobreturno() {
        return turnoPadreId != null;
    }

    @Override
    public String toString() {
        return String.format("Turno [id=%d, fechaHora=%s, médico=%s, paciente=%s]",
                id, fechaHora, medico, paciente);
    }
}
