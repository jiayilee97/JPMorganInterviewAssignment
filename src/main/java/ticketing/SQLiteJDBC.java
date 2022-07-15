package ticketing;

import ticketing.utils.SqlConstants;
import ticketing.utils.TicketStatus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private ResultSet rs = null;

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

    public void close(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            ps.setString(2, TicketStatus.BOUGHT.name());
            rs = ps.executeQuery();

            while (rs.next()) {
                String columnValue = rs.getString("seat_no");
                if (null == columnValue) {
                    continue;
                }
                Character row = columnValue.charAt(0);
                Integer seatNo = Integer.parseInt(columnValue.substring(1));

                // indicate that seat is taken
                try {
                    seatIsTaken[row - 'A'][seatNo - 1] = 1;
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
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

    public void bookSeat(int showNumber, String phone, String[] seatList) throws SQLException {
        String transactionId = null;
        try {
            c = DriverManager.getConnection(driverConnection);

            // only 1 booking per phone per show
            ps = c.prepareStatement(SqlConstants.FETCH_TICKET_BY_PHONE_AND_SHOW_SQL);
            ps.setInt(1, showNumber);
            ps.setString(2, phone);
            ps.setString(3, TicketStatus.BOUGHT.name());
            rs = ps.executeQuery();
            if (rs.next()) {
                String existingTransactionId = rs.getString("transaction_id");
                if (null != existingTransactionId && !existingTransactionId.isEmpty()) {
                    throw new InternalError("Found an existing booking. Only 1 booking per phone per show allowed.");
                }
            }

            // check if seat is booked
            ps = c.prepareStatement(SqlConstants.FETCH_TICKET_BY_SEAT_AND_SHOW_SQL);
            List<String> forbiddenSeats = new ArrayList<>();
            for (String seat : seatList) {
                ps.setInt(1, showNumber);
                ps.setString(2, seat);
                ps.setString(3, TicketStatus.BOUGHT.name());
                rs = ps.executeQuery();

                if(!rs.next()) continue;
                String seatNo = rs.getString("seat_no");
                if(seatNo == null) continue;
                forbiddenSeats.add(seatNo);
            }
            if (!forbiddenSeats.isEmpty()) {
                throw new InternalError("Booking unsuccessful. These seats are booked: " + String.join(",",
                    forbiddenSeats));
            }


            // fetch rows and seats per row
            ps = c.prepareStatement(SqlConstants.FETCH_SHOW_TOTAL_SEATS_SQL);
            ps.setInt(1, showNumber);
            rs = ps.executeQuery();
            int rows = rs.getInt("rows");
            int seatsPerRow = rs.getInt("seats_per_row");

            // book seats
            ps = c.prepareStatement(SqlConstants.BOOK_SEAT_SQL);
            c.setAutoCommit(false);

            // same transaction id and date since it's a single transaction
            transactionId = String.valueOf((long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L); // 10 digit random number
            String currentTime = Instant.now().with(ChronoField.NANO_OF_SECOND,0).toString();

            // Note: running query inside a loop is anti-pattern, thus batch update is used instead
            for (String seat : seatList) {
                if ((seat.charAt(0) - 'A') > rows || Integer.parseInt(seat.substring(1)) > seatsPerRow) throw new ArrayIndexOutOfBoundsException();
                ps.setString(1, currentTime);
                ps.setString(2, TicketStatus.BOUGHT.name());
                ps.setString(3, seat);
                ps.setString(4, phone);
                ps.setInt(5, showNumber);
                ps.setString(6, transactionId);
                ps.addBatch();
            }
            ps.executeBatch();
            c.commit();
            System.out.println("Success! Transaction id is " + transactionId);

        } catch (SQLException e) {
            System.out.println("Error encountered");
            e.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Seat is non-existent. Rejected.");
        } catch (InternalError e) {
            System.out.println(e.getMessage());
        } finally {
            close(ps);
        }

    }

    public void cancel(String transactionId, String phone) {
        try {
            c = DriverManager.getConnection(driverConnection);

            // fetch seats to be cancelled
            ps = c.prepareStatement(SqlConstants.FETCH_SEATS_TO_BE_CANCELLED_SQL);
            ps.setString(1, phone);
            ps.setString(2, transactionId);
            ps.setString(3, TicketStatus.BOUGHT.name());
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new InternalError("No booking found. Nothing cancelled.");
            }

            // throw error if not within cancellation window
            String buyDateStr = rs.getString("buy_date");
            int showId = rs.getInt("show_id");
            if(buyDateStr == null || buyDateStr.isEmpty()) {
                throw new InternalError("No buy date");
            }

            // fetch cancellation window in secs
            ps = c.prepareStatement(SqlConstants.FETCH_CANCEL_WINDOW_BY_SHOW_ID_SQL);
            ps.setInt(1, showId);
            int cancelWindowSecs = ps.executeQuery().getInt("cancel_window_secs");

            Instant buyDate = Instant.parse(buyDateStr);
            if (buyDate.plusSeconds(cancelWindowSecs).isBefore(Instant.now())) {
                throw new InternalError("Cancellation window has closed.");
            }

            // book seats
            ps = c.prepareStatement(SqlConstants.CANCEL_SEAT_SQL);

            ps.setString(1, TicketStatus.CANCELLED.name());
            ps.setString(2, phone);
            ps.setString(3, transactionId);
            ps.setString(4, TicketStatus.BOUGHT.name());
            ps.executeUpdate();
            System.out.println("Cancelled successfully.");

        } catch (SQLException e) {
            System.out.println("Error encountered");
            e.printStackTrace();
        } catch (DateTimeParseException | InternalError e) {
            System.out.println(e.getMessage());
        } finally {
            close(ps);
        }


    }
}
