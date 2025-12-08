package db;

import java.sql.Connection;
import java.sql.SQLException;
import exceptions.ConnectionException;

/**
 * Utilidades para el manejo de transacciones: commit y rollback.
 */
public class DBUtils {

    /**
     * Realiza commit de la transacción en la conexión dada.
     * @param conn Conexión activa
     * @throws ConnectionException si ocurre un error durante el commit
     */
    public static void commit(Connection conn) throws ConnectionException {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.commit();
            }
        } catch (SQLException e) {
            throw new ConnectionException("Error al commitear transacción.", e);
        }
    }

    /**
     * Realiza rollback de la transacción en la conexión dada.
     * @param conn Conexión activa
     * @throws ConnectionException si ocurre un error durante el rollback
     */
    public static void rollback(Connection conn) throws ConnectionException {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            throw new ConnectionException("Error al rollbackear transacción.", e);
        }
    }
}