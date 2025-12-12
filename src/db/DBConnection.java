package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos SQLite.
 * Mantiene una única conexión abierta durante el ciclo de vida de la aplicación.
 * Acá uso full patrón Singleton: una sola conexión abierta a la base de datos a la vez para evitar conflictos.
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
            // porque las transacciones son binarias, se hacen completas o no se hacen.
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
