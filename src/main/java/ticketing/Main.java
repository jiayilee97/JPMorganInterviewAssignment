package ticketing;


import ticketing.utils.Constants;

import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    static Scanner sc;
    private static SQLiteJDBC sqLiteJDBC = new SQLiteJDBC();

    public static void main(String []args){
        sc = new Scanner(System.in);

        System.out.println("__________________________________________________________");
        System.out.println("Welcome to Ticket Booking!");
        System.out.println("__________________________________________________________");

        // setup db
        sqLiteJDBC.setupDb();

        while(true) {
            System.out.print(Constants.COMMAND_PREFIX);
            String instruction = sc.next();
            switch (instruction) {
                case "View":
                    view();
                    break;
                case "Setup":
                    setup();
                    break;
                case "Availability":
                    availability();
                    break;
                default:
                    System.out.println(Constants.INVALID_COMMAND_HELP);
            }

        }
    }

    private static void availability() {
        try {
            int showNumber = sc.nextInt();
            ResultSet rs = sqLiteJDBC.fetchAvailability(showNumber);
            if (rs == null) {
                System.out.println("Empty result");
            }
        } catch (InputMismatchException | IllegalStateException  e) {
            System.out.println("Invalid");
            sc.nextLine();
        }

    }

    private static void view() {
        try {
            int showNumber = sc.nextInt();
            ResultSet rs = sqLiteJDBC.fetchShow(showNumber);
            if (rs == null) {
                System.out.println("Empty result");
            } else {
            }

        } catch (InputMismatchException | IllegalStateException  e) {
            System.out.println("Invalid");
            sc.nextLine();
        }

    }

    private static void setup() {
        try {
            int showNumber = sc.nextInt();
            int rows = sc.nextInt();
            int seatsPerRow = sc.nextInt();
            int cancelWindowInSec = sc.nextInt() * 60;
            sqLiteJDBC.setupNewShow(showNumber, rows, seatsPerRow, cancelWindowInSec);

        } catch (InputMismatchException | IllegalStateException e) {
            System.out.println("Invalid");
            sc.nextLine();
        }
    }
}