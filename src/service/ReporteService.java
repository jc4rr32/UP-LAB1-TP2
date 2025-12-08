//package service;
//
//import dao.MedicoDAO;
//import daoImp.MedicoDAOImpl;
//import exceptions.DAOException;
//import base.Medico;
//
//import java.sql.SQLException;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Servicio encargado de generar reportes basados en datos de turnos.
// */
//public class ReporteService {
//    private final TurnoService turnoService;
//    private final MedicoDAO medicoDao;
//
//    public ReporteService() throws DAOException, SQLException {
//        this.turnoService = new TurnoService();
//        this.medicoDao = new MedicoDAOImpl();
//    }
//
//    /**
//     * Calcula la recaudación total por cada médico en un rango de fechas.
//     * @param desde fecha inicial (solo fecha)
//     * @param hasta fecha final (solo fecha)
//     * @return mapa de Médico a monto recaudado
//     * @throws DAOException en caso de error de acceso a datos
//     */
//    public Map<Medico, Double> recaudacionPorMedico(LocalDate desde, LocalDate hasta) throws DAOException {
//        Map<Medico, Double> resultado = new HashMap<>();
//        List<Medico> medicos = medicoDao.listarMedicos();
//        for (Medico m : medicos) {
//            double monto = turnoService.calcularRecaudacion(
//                m.getId(),
//                desde.atStartOfDay(),
//                hasta.atTime(LocalTime.MAX)
//            );
//            resultado.put(m, monto);
//        }
//        return resultado;
//    }
//}
