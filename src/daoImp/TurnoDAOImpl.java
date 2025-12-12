package daoImp;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import base.Medico;
import base.Paciente;
import base.Turno;
import dao.TurnoDAO;
import db.DBConnection;
import exceptions.DAOException;

public class TurnoDAOImpl implements TurnoDAO {
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection conn;

    public TurnoDAOImpl() throws DAOException {
        try {
            this.conn = DBConnection.getConnection();
            //crearTablaTurnos(); // SOLO SI LA TABLA NO ESTA CREADA
        } catch (SQLException e) {
            throw new DAOException("Error al inicializar DAO de Turno", e);
        }
    }

  /*  private void crearTablaTurnos() throws DAOException {
        String sql = """
            CREATE TABLE IF NOT EXISTS turnos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                fechaHora TEXT NOT NULL,
                medico_id INTEGER NOT NULL,
                paciente_id INTEGER NOT NULL,
                FOREIGN KEY(medico_id) REFERENCES usuarios(id),
                FOREIGN KEY(paciente_id) REFERENCES usuarios(id)
            );
        """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new DAOException("Error al crear tabla turnos", e);
        }
    }*/

    @Override
    public void guardar(Turno turno) throws DAOException {
        String sql = "INSERT INTO turnos (fechaHora, medico_id, paciente_id) VALUES (?, ?, ?)";
        // Usamos this.conn en lugar de abrir una nueva para evitar bloqueos
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, turno.getFechaHora().format(formatter));
            ps.setInt(2, turno.getMedico().getId());
            ps.setInt(3, turno.getPaciente().getId());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new DAOException("Error al guardar turno", e);
        }
    }

    @Override
    public boolean existeTurnoMedico(int idMedico, LocalDateTime fechaHora) throws DAOException {
        String sql = "SELECT COUNT(*) FROM turnos WHERE medico_id = ? AND fechaHora = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idMedico);
            ps.setString(2, fechaHora.format(formatter));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new DAOException("Error al verificar disponibilidad", e);
        }
    }

    @Override
    public List<Turno> listarTodos() throws DAOException {
        String sql = "SELECT t.id, t.fechaHora, " +
                     "m.id as m_id, m.dni as m_dni, m.nombre as m_nom, m.apellido as m_ape, m.email as m_mail, m.honorariosPorConsulta as m_hon, m.obra_social as m_os, " +
                     "p.id as p_id, p.dni as p_dni, p.nombre as p_nom, p.apellido as p_ape, p.email as p_mail, p.obra_social as p_os " +
                     "FROM turnos t " +
                     "JOIN usuarios m ON t.medico_id = m.id " +
                     "JOIN usuarios p ON t.paciente_id = p.id";
                     
        List<Turno> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                // Recuperar Medico (con manejo de nulos para campos nuevos)
                base.ObraSocial osMedico = rs.getString("m_os") != null ? base.ObraSocial.valueOf(rs.getString("m_os")) : null;
                Medico medico = new Medico(
                    rs.getInt("m_id"), rs.getString("m_dni"), rs.getString("m_nom"), 
                    rs.getString("m_ape"), rs.getString("m_mail"), rs.getDouble("m_hon"), osMedico
                );
                
                // Recuperar Paciente
                base.ObraSocial osPaciente = rs.getString("p_os") != null ? base.ObraSocial.valueOf(rs.getString("p_os")) : null;
                Paciente paciente = new Paciente(
                    rs.getInt("p_id"), rs.getString("p_dni"), rs.getString("p_nom"), 
                    rs.getString("p_ape"), rs.getString("p_mail"), osPaciente
                );
                
                LocalDateTime fecha = LocalDateTime.parse(rs.getString("fechaHora"), formatter);
                
                Turno t = new Turno(rs.getInt("id"), fecha, medico, paciente, null);
                lista.add(t);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar turnos", e);
        }
        return lista;
    }
    
    @Override
    public List<Turno> listarPorPaciente(int idPaciente) throws DAOException {
        String sql = "SELECT t.id, t.fechaHora, " +
                     "m.id as m_id, m.dni as m_dni, m.nombre as m_nom, m.apellido as m_ape, m.email as m_mail, m.honorariosPorConsulta as m_hon, m.obra_social as m_os, " +
                     "p.id as p_id, p.dni as p_dni, p.nombre as p_nom, p.apellido as p_ape, p.email as p_mail, p.obra_social as p_os " +
                     "FROM turnos t " +
                     "JOIN usuarios m ON t.medico_id = m.id " +
                     "JOIN usuarios p ON t.paciente_id = p.id " +
                     "WHERE t.paciente_id = ?"; // <--- FILTRO POR PACIENTE

        List<Turno> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    // Recuperar Medico
                    base.ObraSocial osMedico = rs.getString("m_os") != null ? base.ObraSocial.valueOf(rs.getString("m_os")) : null;
                    Medico medico = new Medico(
                        rs.getInt("m_id"), rs.getString("m_dni"), rs.getString("m_nom"), 
                        rs.getString("m_ape"), rs.getString("m_mail"), rs.getDouble("m_hon"), osMedico
                    );
                    
                    // Recuperar Paciente
                    base.ObraSocial osPaciente = rs.getString("p_os") != null ? base.ObraSocial.valueOf(rs.getString("p_os")) : null;
                    Paciente paciente = new Paciente(
                        rs.getInt("p_id"), rs.getString("p_dni"), rs.getString("p_nom"), 
                        rs.getString("p_ape"), rs.getString("p_mail"), osPaciente
                    );
                    
                    LocalDateTime fecha = LocalDateTime.parse(rs.getString("fechaHora"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    
                    Turno t = new Turno(rs.getInt("id"), fecha, medico, paciente, null);
                    lista.add(t);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar turnos del paciente", e);
        }
        return lista;
    }
    
    @Override
    public List<Turno> listarPorMedico(int idMedico) throws DAOException {
        String sql = "SELECT t.id, t.fechaHora, " +
                     "m.id as m_id, m.dni as m_dni, m.nombre as m_nom, m.apellido as m_ape, m.email as m_mail, m.honorariosPorConsulta as m_hon, m.obra_social as m_os, " +
                     "p.id as p_id, p.dni as p_dni, p.nombre as p_nom, p.apellido as p_ape, p.email as p_mail, p.obra_social as p_os " +
                     "FROM turnos t " +
                     "JOIN usuarios m ON t.medico_id = m.id " +
                     "JOIN usuarios p ON t.paciente_id = p.id " +
                     "WHERE t.medico_id = ?"; // <--- FILTRO POR MEDICO

        List<Turno> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMedico);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    // ... (Misma lógica de mapeo que en listarTodos) ...
                    // Podes copiar el bloque while de listarTodos o listarPorPaciente, es idéntico.
                    
                    // Mapeo resumido para el ejemplo:
                    base.ObraSocial osMedico = rs.getString("m_os") != null ? base.ObraSocial.valueOf(rs.getString("m_os")) : null;
                    Medico medico = new Medico(
                        rs.getInt("m_id"), rs.getString("m_dni"), rs.getString("m_nom"), 
                        rs.getString("m_ape"), rs.getString("m_mail"), rs.getDouble("m_hon"), osMedico
                    );
                    
                    base.ObraSocial osPaciente = rs.getString("p_os") != null ? base.ObraSocial.valueOf(rs.getString("p_os")) : null;
                    Paciente paciente = new Paciente(
                        rs.getInt("p_id"), rs.getString("p_dni"), rs.getString("p_nom"), 
                        rs.getString("p_ape"), rs.getString("p_mail"), osPaciente
                    );
                    
                    LocalDateTime fecha = LocalDateTime.parse(rs.getString("fechaHora"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    
                    Turno t = new Turno(rs.getInt("id"), fecha, medico, paciente, null);
                    lista.add(t);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar turnos del médico", e);
        }
        return lista;
    }
}