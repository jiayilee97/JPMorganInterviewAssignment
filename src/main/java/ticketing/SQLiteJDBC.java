package ticketing;

import ticketing.utils.SqlConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteJDBC {
    private Connection c = null;
    private Statement stmt = null;
    private String sql;
    private int numRowsInserted = 0;
    private PreparedStatement ps = null;
    private String driverClassName = "org.sqlite.JDBC";
    private String driverConnection = "jdbc:sqlite:test.db";


    public SQLiteJDBC(){
        if (c == null) {
            this.setupDb();
        }
    }

    public void setupDb() {
        try {
            Class.forName(driverClassName);
            c = DriverManager.getConnection(driverConnection);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();

            // create show table
            sql = SqlConstants.CREATE_SHOW_TABLE_SQL;
            stmt.executeUpdate(sql);

            // create ticket table
            sql = SqlConstants.CREATE_TICKET_TABLE_SQL;
            stmt.executeUpdate(sql);

            stmt.close();
            c.close();
        } catch (SQLException | ClassNotFoundException e ) {
            e.printStackTrace();
            System.err.println("Unable to create tables");
        }
        System.out.println("Table created successfully");
    }

    public int setupNewShow(Integer showNumber, Integer numOfRows, Integer seatsPerRow,
                                    Integer cancelWindowInSec) {
        try {
            c = DriverManager.getConnection(driverConnection);
            ps = c.prepareStatement(SqlConstants.INSERT_SQL);
            ps.setInt(1, showNumber);
            ps.setInt(2, numOfRows);
            ps.setInt(3, seatsPerRow);
            ps.setInt(4, cancelWindowInSec);
            numRowsInserted = ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error encountered");
            e.printStackTrace();
        } finally {
            close(ps);
        }
        return numRowsInserted;

    }

    public static void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println("Error encountered");
        }
    }
}
