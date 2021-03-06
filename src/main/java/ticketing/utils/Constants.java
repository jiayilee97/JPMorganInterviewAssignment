package ticketing.utils;

public interface Constants {
    public static final String COMMANDS_LIST = "Below are the valid commands\n\n" + "(general)\n" +
        "Switchrole <role>\n" + "\tswitch to either admin or buyer\n" + "\n" + "(admin)\n" + "Setup " +
        "<Show Number> <Number of Rows> <Number of seats per row>  <Cancellation window in " +
        "minutes>\n" + "\tset number of seats per show\n" + "View <Show Number> \n" + "\tTo display " +
        "Show Number, Ticket#, Buyer Phone#, Seat Numbers allocated to the buyer\n" + "\n" + "(buyer)" +
        "\n" + "Availability <Show Number>\n" + "\tTo list all available seat numbers for a show. E,g" +
        " A1, F4 etc\n" + "Book <Show Number> <Phone#> <Comma separated list of seats>\n" + "\tTo " +
        "book a ticket. This must generate a unique ticket # and display\n" + "Cancel <Ticket#> " +
        "<Phone#>\n" + "\tTo cancel a ticket";
    public static final String COMMAND_PREFIX = "> ";
    public static final String INVALID = "Invalid command. Type 'Help' for list of commands.";
    public static final int MAX_SEATS_PER_ROW = 10;
    public static final int MAX_ROWS = 26;
}
