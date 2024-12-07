import java.util.Scanner;

public class Interface {
    private String connectionUrl;
    static Database db = new Database();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Welcome to the F1 Database Command Line Interface!");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Explore race statistics, driver performances, lap times, and much more");
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("Enter `h` or 'help' to get the main menu");
        System.out.println("Enter 'q' to close the program");

        while (true) {
            System.out.print("\nUser input: ");
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting the program...");
                break;
            } else if (input.equalsIgnoreCase("h") | input.equalsIgnoreCase("help")) {
                displayMainMenu();
            } else if (input.matches("\\d+[a-zA-Z]")) { // Handles subcategory input like '1a' or '2b'
                handleSubcategory(input.toLowerCase(), scanner);
            } else if (input.matches("\\d+")) { // Handles query selection like '13'
                handleQuery(Integer.parseInt(input));
            } else {
                System.out.println("Invalid input. Enter `h` or 'help' to see the menu or `q` to quit.");
            }
        }

        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("F1 Database main menu:");
        System.out.println("Please select one of the categories to see the available actions.");
        System.out.println("For example, to see Race Queries, enter `1a`");
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
        System.out.println("Enter `h` or 'help' at any time to return to this menu.");
        System.out.println("Type `q` to close the program.");
    }

    private static void handleSubcategory(String subcategory, Scanner scanner) {
        switch (subcategory) {
            case "1a":
                display1a(scanner);
                break;
            case "1b":
                display1b(scanner);
                break;
            case "2a":
                display2a(scanner);
                break;
            case "2b":
                display2b(scanner);
                break;
            case "2c":
                display2c(scanner);
                break;
            case "2d":
                display2d(scanner);
                break;
            case "3a":
                display3a(scanner);
                break;
            case "3b":
                display3b(scanner);
                break;
            case "3c":
                display3c(scanner);
                break;
            case "4a":
                display4a(scanner);
                break;
            default:
                System.out.println("Invalid subcategory. Type `help` to see the menu.");
        }
    }

    private static void display1a(Scanner scanner) {
        System.out.println("1a. Lap Time Performance Queries:");
        System.out.println("1. Find the race with the highest average speed for fastest laps");
        System.out.println("2. Find the fastest lap times across all races");
        System.out.println("3. Fastest lap times in all races");
        System.out.print("Select a query (e.g., '1' or '2'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display1b(Scanner scanner) {
        System.out.println("1b. Race Statistics Queries:");
        System.out.println("4. Count the number of races per year");
        System.out.println("5. Display race information");
        System.out.println("6. Find races with the highest number of accidents and collisions");
        System.out.println("7. Find races with the closest finish");
        System.out.println("8. Find races with below-average participation");
        System.out.print("Select a query (e.g., '4' or '5'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display2a(Scanner scanner) {
        System.out.println("2a. Driver Wins Queries:");
        System.out.println("9. How many races did each driver win?");
        System.out.println("10. Find drivers with a specific number of wins.");
        System.out.println("11. Identify the driver with the most race wins.");
        System.out.print("Select a query (e.g., '9' or '11'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display2b(Scanner scanner) {
        System.out.println("2b. Driver Performance Queries:");
        System.out.println("12. Which driver improved the most throughout the season?");
        System.out.println("13. Who was the worst driver in the season?");
        System.out.println("14. Find the average race position for each driver.");
        System.out.println("15. Sort drivers by total points earned.");
        System.out.println("16. Driver who led the most laps in a race.");
        System.out.println("17. How many drivers were disqualified in the season?");
        System.out.print("Select a query (e.g., '12' or '15'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display2c(Scanner scanner) {
        System.out.println("2c. Driver Race Results Queries:");
        System.out.println("18. Show race results for a specific driver.");
        System.out.println("19. Compare lap times between two drivers in a race.");
        System.out.println("20. Find races in which a specific driver competed.");
        System.out.println("21. Find the total number of pit stops per driver in all races.");
        System.out.print("Select a query (e.g., '18' or '21'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display2d(Scanner scanner) {
        System.out.println("2d. Miscellaneous Driver Queries:");
        System.out.println("22. Identify drivers who have driven for multiple constructors.");
        System.out.print("Select a query (e.g., '22'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display3a(Scanner scanner) {
        System.out.println("3a. Constructor Wins Queries:");
        System.out.println("23. Identify the constructor with the highest points in a season.");
        System.out.println("24. Sort constructors by the number of wins.");
        System.out.println("25. Top constructors across all races.");
        System.out.print("Select a query (e.g., '23' or '24'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display3b(Scanner scanner) {
        System.out.println("3b. Constructor Operational Performance Queries:");
        System.out.println("26. Constructors with the most mechanical failures.");
        System.out.println("27. Which constructor performed the fastest average pit stop?");
        System.out.print("Select a query (e.g., '26' or '27'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display3c(Scanner scanner) {
        System.out.println("3c. Constructor Composition & Insights Queries:");
        System.out.println("28. List constructors and their nationalities.");
        System.out.println("29. Get details of drivers from a specific constructor.");
        System.out.println("30. Find the number of drivers per constructor.");
        System.out.print("Select a query (e.g., '28' or '30'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display4a(Scanner scanner) {
        System.out.println("4a. Races by Location Queries:");
        System.out.println("31. List all countries where races were held.");
        System.out.println("32. List all circuits in a specific country.");
        System.out.print("Select a query (e.g., '31' or '32'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void handleQuery(int queryNumber) {
        System.out.println("Executing query #" + queryNumber + "...");

        switch (queryNumber) {
            case 1:
                db.findRaceWithHighestAvgSpeed();
        }
    }





