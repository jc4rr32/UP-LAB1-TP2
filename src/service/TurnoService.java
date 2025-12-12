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

    public TurnoService() throws DAOException {
        this.turnoDao = new TurnoDAOImpl();
    }

    public void registrarTurno(Turno turno) throws ServiceException, TurnoNoDisponibleException {
        // ELIMINAMOS la conexión local 'conn' aquí porque el DAO ya maneja la suya.
        // Esto evita el error "Database is locked".
        try {
            // Regla de Negocio: Validar Disponibilidad
            if (turnoDao.existeTurnoMedico(turno.getMedico().getId(), turno.getFechaHora())) {
                throw new TurnoNoDisponibleException("El médico ya tiene un turno asignado en esa fecha y hora.");
            }

            // Guardar (El DAO se encarga de la persistencia)
            turnoDao.guardar(turno);
            
        } catch (Exception e) {
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