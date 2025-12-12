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
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection conn;

    public TurnoDAOImpl() throws DAOException {
        try {
            this.conn = DBConnection.getConnection();
        } catch (SQLException e) {
            throw new DAOException("Error al conectar con la BD en TurnoDAO", e);
        }
    }

    @Override
    public void guardar(Turno turno) throws DAOException {
        String sql = "INSERT INTO turnos (fechaHora, medico_id, paciente_id) VALUES (?, ?, ?)";
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
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al verificar disponibilidad", e);
        }
    }

    @Override
    public List<Turno> listarTodos() throws DAOException {
        // AGREGADO: ORDER BY t.fechaHora
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

    private List<Turno> ejecutarConsultaListado(String sql, int idFiltro) throws DAOException {
        List<Turno> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (idFiltro != -1) {
                ps.setInt(1, idFiltro);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    ObraSocial osMedico = rs.getString("m_os") != null ? ObraSocial.valueOf(rs.getString("m_os")) : null;
                    Medico medico = new Medico(
                        rs.getInt("m_id"), rs.getString("m_dni"), rs.getString("m_nom"), 
                        rs.getString("m_ape"), rs.getString("m_mail"), rs.getDouble("m_hon"), osMedico
                    );
                    
                    ObraSocial osPaciente = rs.getString("p_os") != null ? ObraSocial.valueOf(rs.getString("p_os")) : null;
                    Paciente paciente = new Paciente(
                        rs.getInt("p_id"), rs.getString("p_dni"), rs.getString("p_nom"), 
                        rs.getString("p_ape"), rs.getString("p_mail"), osPaciente
                    );
                    
                    LocalDateTime fecha = LocalDateTime.parse(rs.getString("fechaHora"), formatter);
                    
                    Turno t = new Turno(rs.getInt("id"), fecha, medico, paciente, null);
                    lista.add(t);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error al listar turnos", e);
        }
        return lista;
    }
}