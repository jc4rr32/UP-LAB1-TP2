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

/**
 * Implementación DAO de Usuario Maneja CRUD y búsquedas, 
 * por si quiero mostrar Medicos y pacientes en un tablero general
 */
public class UsuarioDAOImpl implements UsuarioDAO {
	private final Connection conn;

	public UsuarioDAOImpl() throws DAOException {
		try {
			this.conn = DBConnection.getConnection();
			// La creación de tablas la debe manejar la DB
			//crearTablaUsuarios();
		} catch (SQLException e) {
			throw new DAOException("Error al inicializar DAO de Usuario", e);
		}
	}

	/* La creación de tablas la debe manejar la DB
	private void crearTablaUsuarios() throws DAOException {
		String sql = "CREATE TABLE IF NOT EXISTS usuarios (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "dni TEXT NOT NULL UNIQUE, " + "nombre TEXT NOT NULL, " + "apellido TEXT NOT NULL, " + "email TEXT, "
				+ "rol TEXT NOT NULL" + ")";
		try (Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new DAOException("Error al crear tabla usuarios", e);
		}
	}*/

	@Override
	public boolean guardar(Usuario usuario) throws DAOException {
	    String sql = """
	        INSERT INTO usuarios
	          (dni, nombre, apellido, email, rol, honorariosPorConsulta)
	        VALUES (?, ?, ?, ?, ?, ?)
	        """;
	    try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setString(1, usuario.getDni());
	        ps.setString(2, usuario.getNombre());
	        ps.setString(3, usuario.getApellido());
	        ps.setString(4, usuario.getEmail());
	        ps.setString(5, usuario.getRol().name());
	        
	        // Solo si el rol es MEDICO tomamos honorarios, en otros casos 0.0
	        double honor = (usuario.getRol() == Rol.MEDICO)
	            ? ((Medico) usuario).getHonorariosPorConsulta()
	            : 0.0;
	        ps.setDouble(6, honor);
	        ps.setString(7, usuario.getObraSocial() != null ? usuario.getObraSocial().name() : null);
	        
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
	           SET dni                   = ?,
	               nombre                = ?,
	               apellido              = ?,
	               email                 = ?,
	               rol                   = ?,
	               honorariosPorConsulta = ?
	         WHERE id = ?
	        """;
	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, usuario.getDni());
	        ps.setString(2, usuario.getNombre());
	        ps.setString(3, usuario.getApellido());
	        ps.setString(4, usuario.getEmail());
	        ps.setString(5, usuario.getRol().name());
	        // Solo si el rol es MEDICO tomamos honorarios, en otros casos 0.0
	        double honor = (usuario.getRol() == Rol.MEDICO)
	            ? ((Medico) usuario).getHonorariosPorConsulta()
	            : 0.0;
	        ps.setDouble(6, honor);

	        ps.setInt(7, usuario.getId());
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
	            while (rs.next()) {
	                list.add(mapRow(rs));
	            }
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
				while (rs.next()) {
					lista.add(mapRow(rs));
				}
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
				while (rs.next()) {
					lista.add(mapRow(rs));
				}
			}
			return lista;
		} catch (SQLException e) {
			throw new DAOException("Error al buscar usuarios por apellido", e);
		}
	}
	
	

	/**
	 * Mapea una fila de ResultSet a la subclase concreta (Medico o Paciente) según
	 * el rol.
	 */
	 private Usuario mapRow(ResultSet rs) throws SQLException, DAOException {
	        int id       = rs.getInt("id");
	        String dni   = rs.getString("dni");
	        String nom   = rs.getString("nombre");
	        String ape   = rs.getString("apellido");
	        String email = rs.getString("email");
	        Rol rol      = Rol.valueOf(rs.getString("rol"));
	        double honor = rs.getDouble("honorariosPorConsulta");
	        
	     // Leemos el string y lo convertimos a Enum (manejando nulos)
	        String osStr = rs.getString("obra_social");
	        ObraSocial os = (osStr != null) ? ObraSocial.valueOf(osStr) : ObraSocial.PARTICULAR;

	        return switch (rol) {
	            case MEDICO   -> new Medico(id, dni, nom, ape, email, honor, os);
	            case PACIENTE -> new Paciente(id, dni, nom, ape, email, os);
	            case ADMIN    -> new Administrador(id, dni, nom, ape, email);
	            default      -> throw new DAOException("Rol desconocido: " + rol);
	        };
	    }

	/**
	 * Consulta la tabla medicos para obtener los honorarios por consulta de un
	 * médico.
	 */
//	private double getHonorariosFromDB(int medicoId) throws DAOException {
//		String sql = "SELECT honorariosPorConsulta FROM medicos WHERE id = ?";
//		try (PreparedStatement ps = conn.prepareStatement(sql)) {
//			ps.setInt(1, medicoId);
//			try (ResultSet rs = ps.executeQuery()) {
//				if (rs.next()) {
//					return rs.getDouble("honorariosPorConsulta");
//				} else {
//					throw new DAOException("No se encontró honorarios para medico con ID " + medicoId);
//				}
//			}
//		} catch (SQLException e) {
//			throw new DAOException("Error al obtener honorarios del medico", e);
//		}
//	}

}
