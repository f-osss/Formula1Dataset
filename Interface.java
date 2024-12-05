import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
/*
public class Interface {
    private String connectionUrl;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Welcome to the F1 Database Command Line Interface!");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Explore race statistics, driver performances, lap times, and much more");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Type in `help` to get the main menu");
        System.out.println("Type in 'Q' or 'q' to close the program");

        while (true) {
            System.out.print("\nUser input: ");
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("Q")) {
                System.out.println("Closing the program. Goodbye!");
                break;
            } else if (input.equalsIgnoreCase("help")) {
                displayMainMenu();
            } else if (input.matches("\\d[a-zA-Z]?")) {
                handleCategory(input.toLowerCase());
            } else {
                System.out.println("Invalid input. Type `help` to see the menu or `Q` to quit.");
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("F1 Database main menu:");
        System.out.println("Please select one of the categories to see the available actions.");
        System.out.println("For example, to see Race Queries, type `1a` or `1 a`");
        System.out.println();
        System.out.println("1. Race Queries");
        System.out.println("   a. Lap Time Performance");
        System.out.println("   b. Race Statistics");
        System.out.println();
        System.out.println("2. Driver Queries");
        System.out.println("   a. Driver Wins");
        System.out.println("   b. Driver Performance");
        System.out.println("   c. Driver Race Results");
        System.out.println("   d. Miscellaneous Driver");
        System.out.println();
        System.out.println("3. Constructor Queries");
        System.out.println("   a. Constructor Wins");
        System.out.println("   b. Constructor Operational Performance");
        System.out.println("   c. Constructor Composition & Insights");
        System.out.println();
        System.out.println("4. Miscellaneous");
        System.out.println("   a. Races by Location");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Type `help` at any time to return to this menu.");
        System.out.println("Type `Q` to close the program.");
    }

    private static void handleCategory(String input) {
        switch (input) {
            case "1a":
                Database.lapTimePerformance();
                break;
            case "1b":
                Database.raceStatistics();
                break;
            case "2a":
                Database.driverWins();
                break;
            case "2b":
                Database.driverPerformance();
                break;
            case "2c":
                Database.driverRaceResults();
                break;
            case "2d":
                Database.miscellaneousDriver();
                break;
            case "3a":
                Database.constructorWins();
                break;
            case "3b":
                Database.constructorOperationalPerformance();
                break;
            case "3c":
                Database.constructorCompositionInsights();
                break;
            case "4a":
                Database.racesByLocation();
                break;
            default:
                System.out.println("Invalid category. Type `help` to see the main menu.");
        }
    }




}
*/