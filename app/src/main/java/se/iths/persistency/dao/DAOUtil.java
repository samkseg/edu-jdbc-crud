package se.iths.persistency.dao;

import java.sql.*;

public class DAOUtil {

    private static final String JDBC_CONNECTION = "jdbc:mysql://localhost:3306/Chinook";
    private static final String JDBC_USER = "iths";
    private static final String JDBC_PASSWORD = "iths";

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

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_CONNECTION,JDBC_USER,JDBC_PASSWORD);
    }


    public static void execute(String sql){
        Connection con = null;
        try {
            con = getConnection();
            getConnection().createStatement().execute(sql);
        } catch (SQLException ignore) {}
        finally {
            close(con);
        }
    }
}