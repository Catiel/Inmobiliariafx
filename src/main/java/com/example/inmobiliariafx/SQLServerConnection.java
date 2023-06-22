package com.example.inmobiliariafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Esta clase proporciona una conexión a la base de datos SQL Server para la aplicación de Inmobiliaria.
 */
public class SQLServerConnection {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=Inmobiliaria";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    /**
     * Obtiene una conexión a la base de datos.
     *
     * @return La conexión a la base de datos.
     * @throws SQLException Si ocurre un error al establecer la conexión.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
}
