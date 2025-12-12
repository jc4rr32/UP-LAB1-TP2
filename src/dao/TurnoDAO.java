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
}