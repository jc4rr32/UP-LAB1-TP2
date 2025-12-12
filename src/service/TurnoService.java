package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import base.Turno;
import dao.TurnoDAO;
import daoImp.TurnoDAOImpl;
import db.DBConnection;
// import db.DBUtils; 
import exceptions.DAOException;
import exceptions.ServiceException;
import exceptions.TurnoNoDisponibleException;

public class TurnoService {
    private final TurnoDAO turnoDao;
    private final Connection conn; // Necesitamos la conexión para hacer commit

    public TurnoService() throws DAOException {
        this.turnoDao = new TurnoDAOImpl();
        try {
            // Obtenemos la MISMA conexión que usa el DAO (es un singleton)
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new DAOException("Error al obtener conexión en Service", e);
        }
    }

    public void registrarTurno(Turno turno) throws ServiceException, TurnoNoDisponibleException {
        try {
            // Validar Disponibilidad (Regla de Negocio)
            if (turnoDao.existeTurnoMedico(turno.getMedico().getId(), turno.getFechaHora())) {
                throw new TurnoNoDisponibleException("El médico ya tiene un turno asignado en esa fecha y hora.");
            }

            // 2. Guardar (El DAO hace el INSERT)
            turnoDao.guardar(turno);
            
            // Confirmar cambios en la base de datos
            conn.commit();
            
        } catch (Exception e) {
            // Si algo falla, deshacemos cualquier cambio pendiente
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            if (e instanceof TurnoNoDisponibleException) {
                throw (TurnoNoDisponibleException) e;
            }
            throw new ServiceException("Error al registrar turno: " + e.getMessage(), e);
        }
    }

    public List<Turno> listarTurnos() throws ServiceException {
        try {
            return turnoDao.listarTodos();
        } catch (DAOException e) {
            throw new ServiceException("Error al listar turnos", e);
        }
    }
    
    public List<Turno> listarTurnosPorPaciente(int idPaciente) throws ServiceException {
        try {
            return turnoDao.listarPorPaciente(idPaciente);
        } catch (DAOException e) {
            throw new ServiceException("Error al listar turnos del paciente", e);
        }
    }
    
    public List<Turno> listarTurnosPorMedico(int idMedico) throws ServiceException {
        try {
            return turnoDao.listarPorMedico(idMedico);
        } catch (DAOException e) {
            throw new ServiceException("Error al obtener turnos del médico", e);
        }
    }
}