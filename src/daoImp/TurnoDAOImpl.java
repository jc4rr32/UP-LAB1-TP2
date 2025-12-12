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
    
    // Formato compatible con SQLite (que guarda fechas como texto)
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void guardar(Turno turno) throws DAOException {
        String sql = "INSERT INTO turnos (fechaHora, medico_id, paciente_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
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
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
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
        // JOIN para traer datos completos del médico y paciente en una sola consulta
        String sql = "SELECT t.id, t.fechaHora, " +
                     "m.id as m_id, m.dni as m_dni, m.nombre as m_nom, m.apellido as m_ape, m.email as m_mail, m.honorariosPorConsulta as m_hon, " +
                     "p.id as p_id, p.dni as p_dni, p.nombre as p_nom, p.apellido as p_ape, p.email as p_mail " +
                     "FROM turnos t " +
                     "JOIN usuarios m ON t.medico_id = m.id " +
                     "JOIN usuarios p ON t.paciente_id = p.id";
                     
        List<Turno> lista = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                Medico medico = new Medico(
                    rs.getInt("m_id"), rs.getString("m_dni"), rs.getString("m_nom"), 
                    rs.getString("m_ape"), rs.getString("m_mail"), rs.getDouble("m_hon")
                );
                Paciente paciente = new Paciente(
                    rs.getInt("p_id"), rs.getString("p_dni"), rs.getString("p_nom"), 
                    rs.getString("p_ape"), rs.getString("p_mail")
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
}