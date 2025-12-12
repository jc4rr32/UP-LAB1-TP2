package service;

import java.sql.Connection;
import java.util.List;
import base.Turno;
import dao.TurnoDAO;
import daoImp.TurnoDAOImpl;
import db.DBConnection;
import db.DBUtils;
import exceptions.DAOException;
import exceptions.ServiceException;
import exceptions.TurnoNoDisponibleException;

public class TurnoService {
    private final TurnoDAO turnoDao;

    public TurnoService() {
        this.turnoDao = new TurnoDAOImpl();
    }

    public void registrarTurno(Turno turno) throws ServiceException, TurnoNoDisponibleException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // Regla de Negocio: Validar Disponibilidad
            if (turnoDao.existeTurnoMedico(turno.getMedico().getId(), turno.getFechaHora())) {
                throw new TurnoNoDisponibleException("El médico ya tiene un turno asignado en esa fecha y hora.");
            }

            // Guardar con transacción
            turnoDao.guardar(turno);
            DBUtils.commit(conn);
            
        } catch (Exception e) {
            try { DBUtils.rollback(conn); } catch (Exception ex) {}
            
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
}