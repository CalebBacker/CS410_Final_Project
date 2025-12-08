// db/Database.java

package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL =
            "jdbc:mysql://localhost:54675/grades?verifyServerCertificate=false&useSSL=true";


    // Sandbox credentials
    private static final String USER = "msandbox";
    private static final String PASS = "BSUdb";

    // Single shared connection
    private static Connection conn;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL, USER, PASS);
        }
        return conn;
    }
}
