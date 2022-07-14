package ticketing.utils;

public interface SqlConstants {
    // defined here to prevent SQL injection
    public static final String INSERT_SQL = "INSERT INTO show (id, rows, seats_per_row, cancel_window_secs) " +
        "VALUES (?, ?, ?, ? );";
    public static final String CREATE_SHOW_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS show " +
            "(id INT PRIMARY KEY     NOT NULL," +
            " rows           INT    " +
            " NOT NULL, " +
            " seats_per_row  INT     NOT NULL, " +
            " cancel_window_secs  INT  NOT NULL)";
    public static final String CREATE_TICKET_TABLE_SQL =
        "CREATE TABLE IF NOT EXISTS ticket " +
            "(id INT PRIMARY KEY     NOT NULL," +
            " buy_date       TEXT " +
            "    NOT NULL, " +
            " status  CHAR(20)     NOT NULL, " +
            " seat_no  INT  NOT NULL, " +
            " buyer_phone " +
            "CHAR(15) NOT NULL," +
            " show_id INT NOT NULL " +
            ")";
}
