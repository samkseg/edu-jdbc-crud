package se.iths.persistency;

import java.sql.Connection;
import java.sql.DriverManager;
public class ConnectToDB {
    private static final String JDBC_CONNECTION = "jdbc:mysql://localhost:3306/iths";
    private static final String JDBC_USER = "iths";
    private static final String JDBC_PASSWORD = "iths";
    public static Connection con = null;

    private ConnectToDB() {

    }
    public static Connection connect() throws Exception {
        con = DriverManager.getConnection(JDBC_CONNECTION, JDBC_USER, JDBC_PASSWORD);
        return con;
    }
}
