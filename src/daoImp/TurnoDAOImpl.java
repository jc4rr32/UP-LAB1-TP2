//TODO: Reutilizar codigo de obtenerReporte

package daoImp;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import base.Medico;
import base.Paciente;
import base.Turno;
import base.ObraSocial;
import dao.TurnoDAO;
import db.DBConnection;
import exceptions.DAOException;

public class TurnoDAOImpl implements TurnoDAO {
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  //esto es porque SQLite no tiene tipo de dato nativo para datetime
    private final Connection conn;
    
    // Constructor para inyección de conexión (usado por Service para transacciones)
    public TurnoDAOImpl(Connection conn) {
        this.conn = conn;
    }

 // Constructor vacío (Obtiene su propia conexión). Para operaciones de lectura y similares
    public TurnoDAOImpl() throws DAOException {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new DAOException("Error al conectar con la BD en TurnoDAO", e);
        }
    }

    @Override
    public void guardar(Turno turno) throws DAOException {
        String sql = "INSERT INTO turnos (fechaHora, medico_id, paciente_id) VALUES (?, ?, ?)"; //uso ? ? ? para evitar SQL injections
        try (PreparedStatement ps = conn.prepareStatement(sql)) { //seteo los parámetros a guardar
            ps.setString(1, turno.getFechaHora().format(formatter));
            ps.setInt(2, turno.getMedico().getId());
            ps.setInt(3, turno.getPaciente().getId());
            ps.executeUpdate(); //acá no hay un conn commit porque la responsabilidad de confirmar el cambio es del servicio
        } catch (SQLException e) {
            throw new DAOException("Error al guardar turno", e);
        }
    }

    @Override
    public boolean existeTurnoMedico(int idMedico, LocalDateTime fechaHora) throws DAOException {
        String sql = "SELECT COUNT(*) FROM turnos WHERE medico_id = ? AND fechaHora = ?"; //cuantos turnos tiene este medico a esta hora en particular
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setString(2, fechaHora.format(formatter));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; //si es mayor a 0, el médico está ocupado en ese horario
            }
        } catch (SQLException e) {
            throw new DAOException("Error al verificar disponibilidad", e);
        }
    }

    @Override
    public List<Turno> listarTodos() throws DAOException { //convierte filas de tablas en objetos
        // AGREGADO: ORDER BY t.fechaHora
    	// Une la tabla turnos con la tabla usuarios por id para ver nombres y no números de id 
        String sql = """
            SELECT t.id, t.fechaHora,
                   m.id as m_id, m.dni as m_dni, m.nombre as m_nom, m.apellido as m_ape, m.email as m_mail, m.honorariosPorConsulta as m_hon, m.obra_social as m_os,
                   p.id as p_id, p.dni as p_dni, p.nombre as p_nom, p.apellido as p_ape, p.email as p_mail, p.obra_social as p_os
            FROM turnos t
            JOIN usuarios m ON t.medico_id = m.id
            JOIN usuarios p ON t.paciente_id = p.id
            ORDER BY t.fechaHora ASC
        """;
        return ejecutarConsultaListado(sql, -1);
    }

    @Override
    public List<Turno> listarPorPaciente(int idPaciente) throws DAOException {
        // AGREGADO: ORDER BY t.fechaHora
        String sql = """
            SELECT t.id, t.fechaHora,
                   m.id as m_id, m.dni as m_dni, m.nombre as m_nom, m.apellido as m_ape, m.email as m_mail, m.honorariosPorConsulta as m_hon, m.obra_social as m_os,
                   p.id as p_id, p.dni as p_dni, p.nombre as p_nom, p.apellido as p_ape, p.email as p_mail, p.obra_social as p_os
            FROM turnos t
            JOIN usuarios m ON t.medico_id = m.id
            JOIN usuarios p ON t.paciente_id = p.id
            WHERE t.paciente_id = ?
            ORDER BY t.fechaHora ASC
        """;
        return ejecutarConsultaListado(sql, idPaciente);
    }

    @Override
    public List<Turno> listarPorMedico(int idMedico) throws DAOException {
        // AGREGADO: ORDER BY t.fechaHora
        String sql = """
            SELECT t.id, t.fechaHora,
                   m.id as m_id, m.dni as m_dni, m.nombre as m_nom, m.apellido as m_ape, m.email as m_mail, m.honorariosPorConsulta as m_hon, m.obra_social as m_os,
                   p.id as p_id, p.dni as p_dni, p.nombre as p_nom, p.apellido as p_ape, p.email as p_mail, p.obra_social as p_os
            FROM turnos t
            JOIN usuarios m ON t.medico_id = m.id
            JOIN usuarios p ON t.paciente_id = p.id
            WHERE t.medico_id = ?
            ORDER BY t.fechaHora ASC
        """;
        return ejecutarConsultaListado(sql, idMedico);
    }

    private List<Turno> ejecutarConsultaListado(String sql, int idFiltro) throws DAOException { //este método lo uso para reutilizar código y no andar copiando y pegando
        List<Turno> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (idFiltro != -1) { //si es -1, no hay filtro
                ps.setInt(1, idFiltro);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) { //lo uso para pasar los datos que obtuve de la db a los objetos correspondientes
                    ObraSocial osMedico = rs.getString("m_os") != null ? ObraSocial.valueOf(rs.getString("m_os")) : null; //obra social del medico
                    //creo objeto medico
                    Medico medico = new Medico(
                        rs.getInt("m_id"), rs.getString("m_dni"), rs.getString("m_nom"), 
                        rs.getString("m_ape"), rs.getString("m_mail"), rs.getDouble("m_hon"), osMedico
                    );
                    
                    //reconstruyo objeto paciente y creo uno nuevo
                    ObraSocial osPaciente = rs.getString("p_os") != null ? ObraSocial.valueOf(rs.getString("p_os")) : null;
                    Paciente paciente = new Paciente(
                        rs.getInt("p_id"), rs.getString("p_dni"), rs.getString("p_nom"), 
                        rs.getString("p_ape"), rs.getString("p_mail"), osPaciente
                    );
                    
                    LocalDateTime fecha = LocalDateTime.parse(rs.getString("fechaHora"), formatter); //convertir string a fechqa
                    
                    Turno t = new Turno(rs.getInt("id"), fecha, medico, paciente, null); //turno final uniendo todo
                    lista.add(t); //guardo en la lista
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar turnos", e);
        }
        return lista;
    }
    
    @Override
    public Object[] obtenerReporteMedico(int idMedico, LocalDateTime desde, LocalDateTime hasta) throws DAOException {
        // Consulta: Une turnos con médicos, filtra por ID y Fechas, y calcula totales
    	//TODO: Manejar mejor estos errores
        String sql = """
            SELECT m.nombre, m.apellido, COUNT(t.id) as cantidad, SUM(m.honorariosPorConsulta) as total
            FROM turnos t
            JOIN usuarios m ON t.medico_id = m.id
            WHERE t.medico_id = ? 
            AND t.fechaHora >= ? AND t.fechaHora <= ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            ps.setString(2, desde.format(formatter));
            ps.setString(3, hasta.format(formatter));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Validamos si trajo datos reales (si count es 0, nombre puede ser NULL)
                    if (rs.getString("nombre") != null) {
                        return new Object[] {
                            rs.getString("nombre"),   // Índice 0
                            rs.getString("apellido"), // Índice 1
                            rs.getInt("cantidad"),    // Índice 2
                            rs.getDouble("total")     // Índice 3
                        };
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al generar reporte individual", e);
        }
        return null;
    }

    @Override
    public List<Object[]> obtenerReporteGeneral(LocalDateTime desde, LocalDateTime hasta) throws DAOException {
        // Consulta: Agrupa por médico para mostrar cuánto recaudó cada uno en el periodo
        String sql = """
            SELECT m.nombre, m.apellido, COUNT(t.id) as cantidad, SUM(m.honorariosPorConsulta) as total
            FROM turnos t
            JOIN usuarios m ON t.medico_id = m.id
            WHERE t.fechaHora >= ? AND t.fechaHora <= ?
            GROUP BY m.id, m.nombre, m.apellido
            ORDER BY total DESC
        """;

        List<Object[]> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, desde.format(formatter));
            ps.setString(2, hasta.format(formatter));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[] {
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getInt("cantidad"),
                        rs.getDouble("total")
                    });
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al generar reporte general", e);
        }
        return lista;
    }
}