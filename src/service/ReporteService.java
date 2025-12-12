package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dao.TurnoDAO;
import daoImp.TurnoDAOImpl;
import db.DBConnection;
import exceptions.DAOException;
import exceptions.DatosInvalidosException;
import exceptions.ServiceException;

/**
 * Servicio encargado de la lógica de negocio relacionada con los Reportes.
 * * Se conecta con el TurnoDAO ya que la información de facturación (recaudación)
 * surge de los turnos registrados en el sistema.
 */
public class ReporteService {
    
    private final TurnoDAO turnoDao;
    private final Connection conn;

    /**
     * Constructor del servicio.
     * Inicializa la conexión y el DAO necesario para obtener los datos.
     * * @throws ServiceException Si ocurre un error al obtener la conexión a la base de datos.
     */
    public ReporteService() throws ServiceException {
        try {
            // Obtenemos la conexión única del sistema (Singleton)
            this.conn = DBConnection.getConnection();
            
            // Instanciamos el DAO de Turnos pasándole la conexión.
            // Usamos TurnoDAO porque los reportes se basan en contar/sumar turnos.
            this.turnoDao = new TurnoDAOImpl(this.conn);
            
        } catch (SQLException e) {
            // Encapsulamos cualquier error de infraestructura en una excepción de servicio
            throw new ServiceException("No se pudo iniciar el servicio de reportes", e);
        }
    }

    /**
     * Genera el reporte de recaudación por médico o general.
     * * Este método valida las fechas ingresadas y decide qué consulta ejecutar en el DAO
     * (si es para un médico específico o para todos).
     * * @param fechaDesde Fecha de inicio del rango de búsqueda.
     * @param fechaHasta Fecha de fin del rango de búsqueda.
     * @param idMedico   ID del médico a consultar. Si es -1, se asume que se pide el reporte de TODOS.
     * @return Una lista de arreglos de objetos (Object[]) donde cada fila representa:
     * [0] = Nombre del Médico (String)
     * [1] = Apellido del Médico (String)
     * [2] = Cantidad de Turnos (Integer)
     * [3] = Total Recaudado (Double)
     * @throws ServiceException Si hay un error en la base de datos.
     * @throws DatosInvalidosException Si las fechas son nulas o el rango es inválido.
     */
    public List<Object[]> generarReporteRecaudacion(LocalDate fechaDesde, LocalDate fechaHasta, int idMedico) 
            throws ServiceException, DatosInvalidosException {
        
        // --- VALIDACIONES DE NEGOCIO ---
        
        // Validamos que las fechas no sean nulas (obligatorio)
        if (fechaDesde == null || fechaHasta == null) {
            throw new DatosInvalidosException("Debe seleccionar las fechas 'Desde' y 'Hasta' para generar el reporte.");
        }
        
        // Validamos coherencia temporal: el inicio no puede ser posterior al fin
        if (fechaDesde.isAfter(fechaHasta)) {
            throw new DatosInvalidosException("La fecha de inicio no puede ser mayor a la fecha de fin.");
        }

        // --- PREPARACIÓN DE DATOS ---
        
        // Convertimos LocalDate (solo fecha) a LocalDateTime (fecha y hora) para cubrir el día completo.
        // Desde: 00:00:00 del día seleccionado
        LocalDateTime desde = LocalDateTime.of(fechaDesde, LocalTime.MIN);
        // Hasta: 23:59:59 del día seleccionado
        LocalDateTime hasta = LocalDateTime.of(fechaHasta, LocalTime.MAX);

        try {
            // --- EJECUCIÓN DE CONSULTA SEGÚN CRITERIO ---
            
            if (idMedico == -1) {
                // CASO A: Reporte General (Opción "Todos los médicos")
                // Llamamos al método del DAO que agrupa y lista a todos
                return turnoDao.obtenerReporteGeneral(desde, hasta);
                
            } else {
                // CASO B: Reporte Individual (Un médico específico)
                // Llamamos al método del DAO que filtra por ID
                Object[] resultado = turnoDao.obtenerReporteMedico(idMedico, desde, hasta);
                
                // El DAO devuelve un único array (una fila) o null si no hay datos.
                // Para mantener consistencia con el retorno (List), lo envolvemos en una lista.
                if (resultado != null) {
                    List<Object[]> lista = new ArrayList<>();
                    lista.add(resultado);
                    return lista;
                } else {
                    // Si no hay datos, devolvemos una lista vacía en lugar de null (buena práctica)
                    return Collections.emptyList();
                }
            }
            
        } catch (DAOException e) {
            // Re-lanzamos como excepción de servicio para que la UI muestre un mensaje amigable
            throw new ServiceException("Error al consultar la base de datos para el reporte", e);
        }
    }
}