package dao;

import java.time.LocalDateTime;
import java.util.List;
import base.Turno;
import exceptions.DAOException;

public interface TurnoDAO {
    void guardar(Turno turno) throws DAOException;
    List<Turno> listarTodos() throws DAOException;
    boolean existeTurnoMedico(int idMedico, LocalDateTime fechaHora) throws DAOException;
    List<Turno> listarPorPaciente(int idPaciente) throws DAOException;
    List<Turno> listarPorMedico(int idMedico) throws DAOException;
    /**
     * Reporte Individual: Obtiene datos agregados de un médico específico.
     * Retorna un Object[] donde:
     * [0] = Nombre (String)
     * [1] = Apellido (String)
     * [2] = Cantidad de Turnos (Integer)
     * [3] = Total Recaudado (Double)
     * Retorna null si no hay datos.
     */
    Object[] obtenerReporteMedico(int idMedico, LocalDateTime desde, LocalDateTime hasta) throws DAOException;

    /**
     * Reporte General: Lista la recaudación de todos los médicos.
     * Retorna una lista de Object[] con la misma estructura que el reporte individual.
     */
    List<Object[]> obtenerReporteGeneral(LocalDateTime desde, LocalDateTime hasta) throws DAOException;
}