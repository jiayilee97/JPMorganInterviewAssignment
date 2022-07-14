package ticketing;

import ticketing.utils.SqlConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public ResultSet fetchShow(int showNumber) {
        ResultSet rs = null;
        try {
            c = DriverManager.getConnection(driverConnection);
            ps = c.prepareStatement(SqlConstants.FETCH_SHOW_SQL);
            ps.setInt(1, showNumber);
            rs = ps.executeQuery();

            print(rs);
        } catch (SQLException e) {
            System.out.println("Error encountered");
            e.printStackTrace();
        } finally {
            close(ps);
            return rs;
        }
    }

    private static void print(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = rs.getString(i);
                System.out.print(rsmd.getColumnName(i) + " " + columnValue);
            }
            System.out.println("");
        }
    }

    public ResultSet fetchAvailability(int showNumber) {
        ResultSet rs = null;
        try {
            Map<Character, Map<Integer, Integer>> hashMap = new ConcurrentHashMap<>();
            c = DriverManager.getConnection(driverConnection);

            // fetch rows and seats per row
            ps = c.prepareStatement(SqlConstants.FETCH_SHOW_TOTAL_SEATS_SQL);
            ps.setInt(1, showNumber);
            rs = ps.executeQuery();
            int rows = rs.getInt("rows");
            int seatsPerRow = rs.getInt("seats_per_row");
            int[][] seatIsTaken = new int[rows][seatsPerRow];
            for (int[] tmp : seatIsTaken) {
                Arrays.fill(tmp, 0);
            }

            // fetch seats
            ps = c.prepareStatement(SqlConstants.FETCH_AVAILABILITY_SQL);
            ps.setInt(1, showNumber);
            rs = ps.executeQuery();

            while (rs.next()) {
                String columnValue = rs.getString("seat_no");
                Character row = columnValue.charAt(0);
                Integer seatNo = Integer.parseInt(columnValue.substring(1));

                // indicate that seat is taken
                seatIsTaken[row - 'A'][seatNo - 1] = 1;
            }

            System.out.println("Available seats");
            for (int i = 0; i < seatIsTaken.length; i++) {
                for (int j = 0; j < seatIsTaken[0].length; j++) {
                    if (seatIsTaken[i][j] == 0){
                        String seat = new StringBuilder(String.valueOf(Character.toChars(i + 'A')))
                            .append(j+1)
                            .toString();
                        System.out.print(seat);
                        System.out.print(" ");
                    }

                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Error encountered");
            e.printStackTrace();
        } finally {
            close(ps);
            System.out.println();
            return rs;
        }
    }
}
