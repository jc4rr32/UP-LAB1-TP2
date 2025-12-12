package service;

import base.Medico;
import base.Turno;
import dao.TurnoDAO;
import daoImp.TurnoDAOImpl;
import exceptions.DAOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteService {
    private TurnoDAO turnoDAO;

    public ReporteService() {
        this.turnoDAO = new TurnoDAOImpl();
    }

    public Map<Medico, Double> calcularRecaudacionTotal() throws DAOException {
        List<Turno> turnos = turnoDAO.listarTodos();
        Map<Medico, Double> reporte = new HashMap<>();

        for (Turno t : turnos) {
            Medico m = t.getMedico();
            // Sumar al acumulador del médico
            reporte.put(m, reporte.getOrDefault(m, 0.0) + t.getCosto());
        }
        return reporte;
    }
}
