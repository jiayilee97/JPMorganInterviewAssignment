package ticketing;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import ticketing.utils.Constants;

import java.util.InputMismatchException;
import java.util.Scanner;

@SpringBootApplication
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
                case "Setup":
                    setup();
                    break;
                default:
                    System.out.println(Constants.INVALID_COMMAND_HELP);
            }

        }
    }

    private static void setup() {
        try {
            int showNumber = sc.nextInt();
            int rows = sc.nextInt();
            int seatsPerRow = sc.nextInt();
            int cancelWindowInSec = sc.nextInt() * 60;
            sqLiteJDBC.setupNewShow(showNumber, rows, seatsPerRow, cancelWindowInSec);

        } catch (InputMismatchException e) {
            System.out.println("Invalid");
            sc.nextLine();
        }
    }
}