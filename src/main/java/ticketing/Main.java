package ticketing;


import ticketing.utils.Constants;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    static Scanner sc;

    public static void main(String []args){
        sc = new Scanner(System.in);

        System.out.println("__________________________________________________________");
        System.out.println("Welcome to Ticket Booking!");
        System.out.println("__________________________________________________________");

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
            String showNumber = sc.next();
            int rows = sc.nextInt();
            int seatsPerRow = sc.nextInt();
            int cancelWindowInSec = sc.nextInt() * 60;
        } catch (InputMismatchException e) {
            System.out.println("Invalid");
            sc.nextLine();
        }
    }
}