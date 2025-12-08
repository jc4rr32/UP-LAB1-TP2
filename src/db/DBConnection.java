package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos SQLite.
 * Mantiene una única conexión abierta durante el ciclo de vida de la aplicación.
 */
public class DBConnection {
    private static final String URL = "jdbc:sqlite:turnera.db";
    private static Connection conn;

    // Constructor privado para evitar instanciación
    private DBConnection() {}

    /**
     * Devuelve la conexión única a la base de datos. Si no existe o está cerrada, la crea.
     * @return Connection abierta
     * @throws SQLException si ocurre un error al conectar
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
            // gestionamos transacciones manualmente
            // de esta manera, si tenemos varias operaciones (insertar turno y actualizar agenda) si falla alguna rollbackeamos todas y no individualmente
            conn.setAutoCommit(false); 
        }
        return conn;
    }

    /**
     * Cierra la conexión a la base de datos, si está abierta.
     */
    public static synchronized void closeConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
