package service;

import dao.UsuarioDAO;
import daoImp.UsuarioDAOImpl;
import db.DBUtils;
import db.DBConnection;
import exceptions.ConnectionException;
import exceptions.DAOException;
import exceptions.DatosInvalidosException;
import utils.ValidationUtils;
import base.Medico;
import base.Paciente;
import base.Rol;
import base.Usuario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de usuarios (médicos y pacientes). Incluye
 * validaciones y manejo de transacciones.
 */
public class UsuarioService {
	private final UsuarioDAO usuarioDao;
	private final Connection conn;

	/**
	 * Constructor que inicializa el DAO y obtiene la conexión única.
	 * 
	 * @throws DAOException si ocurre un error al inicializar DAO
	 * @throws SQLException si falla la conexión
	 */
	public UsuarioService() throws DAOException, SQLException {
		this.usuarioDao = new UsuarioDAOImpl();
		this.conn = DBConnection.getConnection();
	}

	/**
	 * Crea un nuevo usuario tras validar campos.
	 * 
	 * @throws DAOException si ocurre un error al inicializar DAO
	 * @throws SQLException si falla la conexión
	 */
	public void crearUsuario(Usuario usuario) throws DAOException, DatosInvalidosException, ConnectionException {
		// Validar campos obligatorios
		ValidationUtils.validarNoVacio(usuario.getDni(), usuario.getNombre(), usuario.getApellido());
		ValidationUtils.validarDni(usuario.getDni());
		// Si el buscarPorDni devuelve algo distinto de null, significa que ya está ocupado.
	    if (usuarioDao.buscarPorDni(usuario.getDni()) != null) {
	        throw new DatosInvalidosException("El usuario con DNI " + usuario.getDni() + " ya se encuentra registrado.");
	    }

		try {
			boolean ok = usuarioDao.guardar(usuario);
			if (!ok)
				throw new DAOException("No se pudo guardar el usuario.");
			DBUtils.commit(conn);
		} catch (Exception e) {
			try {
				DBUtils.rollback(conn);
			} catch (ConnectionException rollbackEx) {
				throw new ConnectionException("Error al rollbackear transaccion: " + rollbackEx.getMessage(),
						rollbackEx);
			}
			if (e instanceof DAOException)
				throw (DAOException) e;
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Actualiza un usuario existente.
	 * 
	 * @throws ConnectionException
	 */
	
	public void actualizarUsuario(Usuario usuario) throws DAOException, DatosInvalidosException, ConnectionException {
		ValidationUtils.validarNoVacio(usuario.getDni(), usuario.getNombre(), usuario.getApellido());
		ValidationUtils.validarDni(usuario.getDni());

		try {
			boolean ok = usuarioDao.actualizar(usuario);
			if (!ok)
				throw new DAOException("No se pudo actualizar el usuario.");
			DBUtils.commit(conn);
		} catch (Exception e) {
			try {
				DBUtils.rollback(conn);
			} catch (ConnectionException rollbackEx) {
				throw new ConnectionException("Error al rollbackear transaccion: " + rollbackEx.getMessage(),
						rollbackEx);
			}
			if (e instanceof DAOException)
				throw (DAOException) e;
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Elimina un usuario por su ID.
	 * 
	 * @throws ConnectionException
	 */
	public void eliminarUsuario(int id) throws DAOException, ConnectionException {
		try {
			boolean ok = usuarioDao.eliminar(id);
			if (!ok)
				throw new DAOException("No se pudo eliminar el usuario.");
			DBUtils.commit(conn);
		} catch (Exception e) {
			try {
				DBUtils.rollback(conn);
			} catch (ConnectionException rollbackEx) {
				throw new ConnectionException("Error al rollbackear transaccion: " + rollbackEx.getMessage(),
						rollbackEx);
			}
			if (e instanceof DAOException)
				throw (DAOException) e;
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca un usuario por DNI.
	 */
	public Usuario buscarPorDni(String dni) throws DAOException, DatosInvalidosException {
		ValidationUtils.validarDni(dni);
		return usuarioDao.buscarPorDni(dni);
	}
	
	/**
     * Busca un usuario por su ID.
     */
    public Usuario buscarPorId(int id) throws DAOException, DatosInvalidosException {
        Usuario u = usuarioDao.buscarPorId(id);
        if (u == null) {
            throw new DatosInvalidosException("Usuario con ID " + id + " no existe.");
        }
        return u;
    }

	/**
	 * Lista todos los usuarios.
	 */
	public List<Usuario> listarUsuarios() throws DAOException {
		return usuarioDao.listarTodos();
	}

	/** Lista todos los usuarios con un rol dado. */
    public List<Usuario> listarPorRol(Rol rol) throws DAOException {
        return usuarioDao.buscarPorRol(rol);
    }
    
    /** Lista solo los médicos (rol = MEDICO). */
    public List<Medico> listarMedicos() throws DAOException {
        List<Usuario> all = listarPorRol(Rol.MEDICO);
        List<Medico> med = new ArrayList<>();
        for (Usuario u : all) {
            med.add((Medico) u);
        }
        return med;
    }

    /** Lista solo los pacientes (rol = PACIENTE). */
    public List<Paciente> listarPacientes() throws DAOException {
        List<Usuario> all = listarPorRol(Rol.PACIENTE);
        List<Paciente> pac = new ArrayList<>();
        for (Usuario u : all) {
            pac.add((Paciente) u);
        }
        return pac;
    }
	
	/**
	 * Busca usuarios por nombre.
	 */
	public List<Usuario> buscarPorNombre(String nombre) throws DAOException {
		return usuarioDao.buscarPorNombre(nombre);
	}

	/**
	 * Busca usuarios por apellido.
	 */
	public List<Usuario> buscarPorApellido(String apellido) throws DAOException {
		return usuarioDao.buscarPorApellido(apellido);
	}
}
