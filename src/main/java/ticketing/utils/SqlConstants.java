package ticketing.utils;

public interface SqlConstants {
    // defined here to prevent SQL injection
    public static final String INSERT_SQL = "INSERT INTO show (id, rows, seats_per_row, cancel_window_secs) " +
        "VALUES (?, ?, ?, ? );";
    public static final String CREATE_SHOW_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS show " +
            "(id INTEGER PRIMARY KEY     NOT NULL," +
            " rows           INT NOT NULL, " +
            " seats_per_row  INT     NOT NULL, " +
            " cancel_window_secs  INT  NOT NULL)";
    public static final String CREATE_TICKET_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS ticket " +
            "(id INTEGER PRIMARY KEY     NOT NULL," +
            " buy_date       TEXT NOT NULL, " +
            " status  CHAR(20)     NOT NULL, " +
            " seat_no  CHAR(4)  NOT NULL, " +
            " buyer_phone CHAR(15) NOT NULL," +
            " show_id INT NOT NULL, " +
            " transaction_id TEXT NOT NULL " +
            ")";
    public static final String FETCH_SHOW_SQL =
        "SELECT t.* FROM show s " +
            "LEFT JOIN ticket t " +
            "ON s.id = t.show_id " +
            "WHERE " +
            "s.id = ? " +
            ";";
    public static final String FETCH_AVAILABILITY_SQL =
        "SELECT t.seat_no FROM show s " +
            "LEFT JOIN ticket t " +
            "ON s.id = t.show_id " +
            "WHERE " +
            "s.id = ? AND " +
            "status = ? " +
            ";";
    public static final String FETCH_SHOW_TOTAL_SEATS_SQL =
        "SELECT s.rows, s.seats_per_row FROM show s " +
            "WHERE " +
            "s.id = ? " +
            ";";
    public static final String BOOK_SEAT_SQL =
        "INSERT INTO ticket (" +
            "buy_date, " +
            "status, " +
            "seat_no, " +
            "buyer_phone, " +
            "show_id," +
            "transaction_id" +
            ") VALUES (" +
            "?, " +
            "?, " +
            "?, " +
            "?, " +
            "?, " +
            "?  " +
            ");";
    public static final String CANCEL_SEAT_SQL =
        "UPDATE ticket SET " +
            "status = ? " +
            "WHERE " +
            "buyer_phone = ? AND " +
            "transaction_id = ? AND " +
            "status = ? " +
            ";";
    public static final String FETCH_SEATS_TO_BE_CANCELLED_SQL =
        "SELECT t.buy_date, t.show_id FROM ticket t " +
            "WHERE " +
            "buyer_phone = ? AND " +
            "transaction_id = ? AND " +
            "status = ? " +
            ";";
    public static final String FETCH_CANCEL_WINDOW_BY_SHOW_ID_SQL =
        "SELECT s.cancel_window_secs FROM show s " +
            "WHERE " +
            "s.id = ? " +
            ";";
    public static final String FETCH_TICKET_BY_PHONE_AND_SHOW_SQL =
        "SELECT t.* FROM ticket t " +
            "WHERE " +
            "t.show_id = ? AND " +
            "t.buyer_phone = ? AND " +
            "t.status = ?" +
            ";";
    public static final String FETCH_TICKET_BY_SEAT_AND_SHOW_SQL =
        "SELECT t.* FROM ticket t " +
            "WHERE " +
            "t.show_id = ? AND " +
            "t.seat_no = ? AND " +
            "t.status = ?" +
            ";";
}
