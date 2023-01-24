package se.iths.persistency;

import java.sql.*;

public class ConnectionHandler {
    private static final String JDBC_CONNECTION = "jdbc:mysql://localhost:3306/Chinook";
    private static final String JDBC_USER = "iths";
    private static final String JDBC_PASSWORD = "iths";
    public static Connection con = null;

    public static void close(Connection con){
        if (con == null) return;
        try {
            con.close();
        } catch (SQLException ignore) {}
    }

    public static void close(ResultSet rs){
        if (rs == null) return;
        try {
            rs.close();
        } catch (SQLException ignore) {}
    }

    public static void close(Statement stmt){
        if (stmt == null) return;
        try {
            stmt.close();
        } catch (SQLException ignore) {}
    }
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(JDBC_CONNECTION, JDBC_USER, JDBC_PASSWORD);
    }
}
