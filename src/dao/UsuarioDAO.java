/*
 * DAO: define que acciones se pueden hacer (guardar, borrar, listar, etc) pero no como se hacen.
 * Me sirve para cambiar la base de datos en el futuro sin romper todo el programa.
*/
package dao;

import java.util.List;

import base.Rol;
import base.Usuario;
import exceptions.DAOException;

/**
 * Interfaz DAO para operaciones ABM genéricas sobre usuarios.
 * Define métodos para crear, leer, actualizar y eliminar usuarios.
 */
public interface UsuarioDAO {
    /** Inserta un nuevo usuario en la db. */
    boolean guardar(Usuario usuario) throws DAOException;

    /** Actualiza los datos de un usuario existente. */
    boolean actualizar(Usuario usuario) throws DAOException;

    /** Elimina un usuario por su ID. */
    boolean eliminar(int id) throws DAOException;

    /** Busca un usuario por su ID. */
    Usuario buscarPorId(int id) throws DAOException;

    /** Busca un usuario por su DNI. */
    Usuario buscarPorDni(String dni) throws DAOException;

    /** Lista todos los usuarios almacenados. */
    List<Usuario> listarTodos() throws DAOException;
    
    /**  Lista usuarios según rol */
    List<Usuario> buscarPorRol(Rol rol) throws DAOException;

    /** Busca usuarios por nombre (o parte de él). */
    List<Usuario> buscarPorNombre(String nombre) throws DAOException;

    /** Busca usuarios por apellido (o parte de él). */
    List<Usuario> buscarPorApellido(String apellido) throws DAOException;
}
