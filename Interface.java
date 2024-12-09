import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Interface {
    static Database db = new Database();
    static Populate po = new Populate();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Welcome to the F1 Database Command Line Interface!");
        System.out.println("Explore Formula 1 Races, Drivers, Constructors and much more...");
        System.out.println("-------------------------------------------------------------------------");

        while (true) {
            System.out.println("Enter `h` or 'help' to get the main menu");
            System.out.println("Enter 'q' to close the program");
            System.out.println("Enter `d` to delete database data and create tables");
            System.out.println("Enter `r` to repopulate database data");
            System.out.print(">>> User input: ");

            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.out.println("Exiting the program...");
                System.out.println("Thank you for using our F1 Database Command Line Interface!");
                break;
            } else if (input.equalsIgnoreCase("h") | input.equalsIgnoreCase("help")) {
                displayMainMenu();
            } else if (input.matches("\\d+\\s?[a-zA-Z]")) {
                handleSubcategory(input.toLowerCase(), scanner);
            } else if (input.matches("\\d+")) {
                handleQuery(Integer.parseInt(input));
            } else if (input.equalsIgnoreCase("d")) {
                try {
                    db.executeSqlFile("formula1_cpg12.sql");

                    System.out.println("Tables deleted and created again successfully\n");
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else if (input.equalsIgnoreCase("r")) {
                try {
                    po.loadConfigAndPopulate();
                    System.out.println("Tables populated successfully");
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else {
                System.out.println("Invalid input.");
                System.out.println("-------------------------------------------------------------------------");
            }
        }
        scanner.close();
    }

    private static void displayMainMenu() {
        System.out.println("-------------------------------------------------------------------------");
        System.out.println("F1 Database main menu:");
        System.out.println("Please select one of the categories to see the available queries.");
        System.out.println("For example, to see Driver wins, enter `2a`");
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
    }

    private static void handleSubcategory(String subcategory, Scanner scanner) {
        System.out.println("-------------------------------------------------------------------------");
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
                System.out.println("Invalid subcategory.");
        }
    }

    private static void display1a(Scanner scanner) {
        System.out.println("1a. Lap Time Performance Queries:");
        System.out.println("1. Find the race with the highest average speed for fastest laps");
        System.out.println("2. Find the fastest lap times for each driver across all races");
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
        System.out.println("12. Top drivers in a specific race");
        System.out.print("Select a query (e.g., '9' or '11'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display2b(Scanner scanner) {
        System.out.println("2b. Driver Performance Queries:");
        System.out.println("13. Which driver improved the most throughout the season?");
        System.out.println("14. Who was the worst driver in the season?");
        System.out.println("15. Find the average race position for each driver.");
        System.out.println("16. Sort drivers by total points earned.");
        System.out.println("17. Driver who led the most laps in a race.");
        System.out.println("18. How many drivers were disqualified in the season?");
        System.out.print("Select a query (e.g., '12' or '15'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display2c(Scanner scanner) {
        System.out.println("2c. Driver Race Results Queries:");
        System.out.println("19. Show race results for a specific driver.");
        System.out.println("20. Compare lap times between two drivers in a race.");
        System.out.println("21. Find races in which a specific driver competed.");
        System.out.println("22. Find the total number of pit stops per driver in all races.");
        System.out.print("Select a query (e.g., '18' or '21'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display2d(Scanner scanner) {
        System.out.println("2d. Miscellaneous Driver Queries:");
        System.out.println("23. Identify drivers who have driven for multiple constructors.");
        System.out.print("Select a query (e.g., '22'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display3a(Scanner scanner) {
        System.out.println("3a. Constructor Wins Queries:");
        System.out.println("24. Identify the constructor with the highest points in a season.");
        System.out.println("25. Sort constructors by the number of wins.");
        System.out.println("26. Top constructors across all races.");
        System.out.print("Select a query (e.g., '23' or '24'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display3b(Scanner scanner) {
        System.out.println("3b. Constructor Operational Performance Queries:");
        System.out.println("27. Constructors with the most mechanical failures.");
        System.out.println("28. Which constructor performed the fastest average pit stop?");
        System.out.print("Select a query (e.g., '26' or '27'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display3c(Scanner scanner) {
        System.out.println("3c. Constructor Composition & Insights Queries:");
        System.out.println("29. List constructors and their nationalities.");
        System.out.println("30. Get details of drivers from a specific constructor.");
        System.out.println("31. Find the number of drivers per constructor.");
        System.out.print("Select a query (e.g., '28' or '30'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }

    private static void display4a(Scanner scanner) {
        System.out.println("4a. Races by Location Queries:");
        System.out.println("32. List all countries where races were held.");
        System.out.println("33. List all circuits in a specific country.");
        System.out.print("Select a query (e.g., '31' or '32'): ");
        handleQuery(Integer.parseInt(scanner.nextLine().trim()));
    }


    private static void handleQuery(int queryNumber) {
        System.out.println("\nExecuting query #" + queryNumber + "...");
        Scanner scanner = new Scanner(System.in);
        switch (queryNumber) {
            case 1:
                System.out.print("Enter limit: ");
                db.findRaceWithHighestAvgSpeed(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 2:
                db.findFastestLapTimesForDriver();
                break;
            case 3:
                System.out.print("Enter limit: ");
                db.fastestLapTimes(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 4:
                db.countRacesPerYear();
                break;
            case 5:
                db.displayRaceInformation();
                break;
            case 6:
                System.out.print("Enter limit: ");
                db.racesWithHighAccidents(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 7:
                System.out.print("Enter limit: ");
                db.findClosestFinish(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 8:
                db.findRacesWithBelowAverageParticipation();
                break;
            case 9:
                db.racesDriverWon();
                break;
            case 10:
                System.out.print("Enter number of wins: ");
                db.findDriversWithSpecificWins(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 11:
                System.out.print("Enter limit: ");
                db.driverWithMostWins(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 12:
                System.out.print("Enter raceID: ");
                int input6 = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter limit: ");
                int input7 = Integer.parseInt(scanner.nextLine().trim());
                db.findTopDriversInRace(input6,input7);
                break;
            case 13:
                db.improvedDriver();
                break;
            case 14:
                db.worstDriver();
                break;
            case 15:
                db.findAverageRacePosition();
                break;
            case 16:
                db.sortDriversByPoints();
                break;
            case 17:
                System.out.print("Enter raceID: ");
                int input1 = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter limit: ");
                int input2 = Integer.parseInt(scanner.nextLine().trim());
                db.findDriverLedMostLaps(input1,input2);
                break;
            case 18:
                System.out.print("Enter year: ");
                db.driversDisqualified(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 19:
                System.out.print("Enter driverID: ");
                db.findRaceResultsByDriver(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 20:
                System.out.print("Enter raceID: ");
                int input3 = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter driverID1: ");
                int input4 = Integer.parseInt(scanner.nextLine().trim());
                System.out.print("Enter driverID2: ");
                int input5 = Integer.parseInt(scanner.nextLine().trim());
                db.compareLapTimes(input3,input4,input5);
                break;
            case 21:
                System.out.print("Enter driverID: ");
                db.racesForDriver(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 22:
                db.totalPitStopsPerDriver();
                break;
            case 23:
                db.findDriversWithMultipleConstructors();
                break;
            case 24:
                System.out.print("Enter limit: ");
                db.findConstructorHighestPoints(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 25:
                System.out.print("Enter limit: ");
                db.constructorsByWins();
                break;
            case 26:
                System.out.print("Enter limit: ");
                db.topConstructors(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 27:
                System.out.print("Enter limit: ");
                db.constructorMostMechanicalFailures(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 28:
                System.out.print("Enter limit: ");
                db.fastestAveragePitStop(Integer.parseInt(scanner.nextLine().trim()));
                break;
            case 29:
                db.listConstructorsAndNationalities();
                break;
            case 30:
                System.out.print("Enter constructor name: ");
                db.driversByConstructor(scanner.nextLine().trim());
                break;
            case 31:
                db.findDriversPerConstructor();
                break;
            case 32:
                db.listAllCountriesWithRaces();
                break;
            case 33:
                System.out.print("Enter country name: ");
                db.listCircuitInCountry(scanner.nextLine().trim());
                break;
            default:
                System.out.println("Invalid query number.");
        }
        System.out.println("-------------------------------------------------------------------------");

    }


}





