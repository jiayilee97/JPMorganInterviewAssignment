package ticketing;


import ticketing.utils.Constants;

import java.util.Scanner;

public class Main {

    public static void main(String []args){
        Scanner sc = new Scanner(System.in);

        System.out.println("__________________________________________________________");
        System.out.println("Welcome to Ticket Booking!");
        System.out.println("__________________________________________________________");

        while(true) {
            System.out.print(Constants.COMMAND_PREFIX);
            String instruction = sc.next();
            switch (instruction) {
                default:
                    System.out.println(Constants.INVALID_COMMAND_HELP);
            }

            // new line to demarcate end of iteration
            System.out.println();

        }



    }
}