package daoImp;

import dao.UsuarioDAO;
import exceptions.DAOException;
import base.Administrador;
import base.Medico;
import base.ObraSocial;
import base.Paciente;
import base.Rol;
import base.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import db.DBConnection;

public class UsuarioDAOImpl implements UsuarioDAO {
    private final Connection conn;

    public UsuarioDAOImpl() throws DAOException {
        try {
            this.conn = DBConnection.getConnection();
            // Eliminada la llamada a crearTablaUsuarios()
        } catch (SQLException e) {
            throw new DAOException("Error al inicializar DAO de Usuario", e);
        }
    }

    @Override
    public boolean guardar(Usuario usuario) throws DAOException {
        String sql = """
            INSERT INTO usuarios
              (dni, nombre, apellido, email, rol, honorariosPorConsulta, obra_social)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getDni());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getRol().name());
            
            double honor = (usuario.getRol() == Rol.MEDICO) ? ((Medico) usuario).getHonorariosPorConsulta() : 0.0;
            ps.setDouble(6, honor);
            
            if (usuario.getObraSocial() != null) {
                ps.setString(7, usuario.getObraSocial().name());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }
            
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    usuario.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new DAOException("Error al guardar usuario", e);
        }
    }

    @Override
    public boolean actualizar(Usuario usuario) throws DAOException {
        String sql = """
            UPDATE usuarios
               SET dni = ?, nombre = ?, apellido = ?, email = ?, rol = ?, honorariosPorConsulta = ?, obra_social = ?
             WHERE id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getDni());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, usuario.getApellido());
            ps.setString(4, usuario.getEmail());
            ps.setString(5, usuario.getRol().name());
            
            double honor = (usuario.getRol() == Rol.MEDICO) ? ((Medico) usuario).getHonorariosPorConsulta() : 0.0;
            ps.setDouble(6, honor);

            if (usuario.getObraSocial() != null) {
                ps.setString(7, usuario.getObraSocial().name());
            } else {
                ps.setNull(7, Types.VARCHAR);
            }

            ps.setInt(8, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar usuario", e);
        }
    }

    @Override
    public boolean eliminar(int id) throws DAOException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException("Error al eliminar usuario", e);
        }
    }

    @Override
    public Usuario buscarPorId(int id) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuario por ID", e);
        }
    }

    @Override
    public Usuario buscarPorDni(String dni) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE dni = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuario por DNI", e);
        }
    }

    @Override
    public List<Usuario> listarTodos() throws DAOException {
        String sql = "SELECT * FROM usuarios";
        List<Usuario> lista = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al listar usuarios", e);
        }
    }
    
    @Override
    public List<Usuario> buscarPorRol(Rol rol) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE rol = ?";
        List<Usuario> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rol.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuarios por rol", e);
        }
    }

    @Override
    public List<Usuario> buscarPorNombre(String nombre) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE nombre LIKE ?";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuarios por nombre", e);
        }
    }

    @Override
    public List<Usuario> buscarPorApellido(String apellido) throws DAOException {
        String sql = "SELECT * FROM usuarios WHERE apellido LIKE ?";
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + apellido + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al buscar usuarios por apellido", e);
        }
    }

    private Usuario mapRow(ResultSet rs) throws SQLException, DAOException {
        int id = rs.getInt("id");
        String dni = rs.getString("dni");
        String nom = rs.getString("nombre");
        String ape = rs.getString("apellido");
        String email = rs.getString("email");
        Rol rol = Rol.valueOf(rs.getString("rol"));
        double honor = rs.getDouble("honorariosPorConsulta");
        String osStr = rs.getString("obra_social");
        ObraSocial os = (osStr != null) ? ObraSocial.valueOf(osStr) : ObraSocial.PARTICULAR;

        return switch (rol) {
            case MEDICO -> new Medico(id, dni, nom, ape, email, honor, os);
            case PACIENTE -> new Paciente(id, dni, nom, ape, email, os);
            case ADMIN -> new Administrador(id, dni, nom, ape, email);
            default -> throw new DAOException("Rol desconocido: " + rol);
        };
    }
}