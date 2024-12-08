import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class Database {
    private Connection connection;

    public Database() {
        Properties prop = new Properties();
        String fileName = "auth.cfg";

        try {
            FileInputStream configFile = new FileInputStream(fileName);
            prop.load(configFile);
            configFile.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find config file.");
            System.exit(1);
        } catch (IOException ex) {
            System.out.println("Error reading config file.");
            System.exit(1);
        }

        String username = prop.getProperty("username");
        String password = prop.getProperty("password");

        if (username == null || password == null) {
            System.out.println("Username or password not provided.");
            System.exit(1);
        }

        String connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
                + "database=cs3380;"
                + "user=" + username + ";"
                + "password=" + password + ";"
                + "encrypt=false;"
                + "trustServerCertificate=false;"
                + "loginTimeout=30;";

        try {
            connection = DriverManager.getConnection(connectionUrl);
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    //Queries
    //1. How many races did each driver win
    public void racesDriverWon() {
        String sql = """
                    SELECT Driver.driverID, Driver.forename, Driver.surname, COUNT(Result.resultID) AS raceWon
                    FROM Result
                    JOIN Driver ON Result.driverID = Driver.driverID
                    WHERE Result.positionOrder = 1
                    GROUP BY Driver.driverID, Driver.forename, Driver.surname
                    ORDER BY raceWon DESC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Drivers and the Number of Races They Won:");
            System.out.printf("%-10s %-15s %-15s %-10s%n", "Driver ID", "Forename", "Surname", "Races Won");
            System.out.println("---------------------------------------------------------");
            while (resultSet.next()) {
                System.out.printf("%-10d %-15s %-15s %-10d%n",
                        resultSet.getInt("driverID"),
                        resultSet.getString("forename"),
                        resultSet.getString("surname"),
                        resultSet.getInt("raceWon"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //2. How many drivers were disqualified in the season
    public void driversDisqualified(int year) {
        String sql = """
                    SELECT Race.year, COUNT(DISTINCT Result.driverID) AS driversDisqualified
                    FROM Result
                    JOIN Race ON Result.raceID = Race.raceID
                    JOIN Status ON Result.statusID = Status.statusID
                    WHERE Status.statusID = 2
                    AND Race.year = ?
                    GROUP BY Race.year;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, year);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.printf("Disqualified Drivers in %d:%n", year);
                System.out.printf("%-10s %-20s%n", "Year", "Drivers Disqualified");
                System.out.println("------------------------------------");

                while (resultSet.next()) {
                    System.out.printf("%-10d %-20d%n",
                            resultSet.getInt("year"),
                            resultSet.getInt("driversDisqualified"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //3. Which driver improved the most throughout the season
    public void improvedDriver() {
        String sql = """
                    WITH DriverSeasonPoints AS (
                        SELECT
                            Driver.driverID,
                            Driver.forename,
                            Driver.surname,
                            SUM(CASE WHEN Race.round = 1 THEN DriverStanding.points ELSE 0 END) AS seasonStartPoints,
                            SUM(CASE WHEN Race.round = (SELECT MAX(round) FROM Race WHERE year = Race.year) THEN DriverStanding.points ELSE 0 END) AS seasonEndPoints
                        FROM
                            DriverStanding
                        JOIN
                            Driver ON DriverStanding.driverID = Driver.driverID
                        JOIN
                            Race ON DriverStanding.raceID = Race.raceID
                        GROUP BY
                            Driver.driverID, Driver.forename, Driver.surname, Race.year
                    )
                    SELECT TOP 1
                        driverID,
                        forename,
                        surname,
                        (seasonEndPoints - seasonStartPoints) AS improvement
                    FROM
                        DriverSeasonPoints
                    ORDER BY
                        improvement DESC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int driverID = resultSet.getInt("driverID");
                String forename = resultSet.getString("forename");
                String surname = resultSet.getString("surname");
                int improvement = resultSet.getInt("improvement");

                System.out.println("Driver with the most improvement:");
                System.out.printf("ID: %d, Name: %s %s, Improvement: %d points%n", driverID, forename, surname, improvement);
            } else {
                System.out.println("No data found for most improved driver.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //4. Constructors who have the most mechanical failures
    public void constructorMostMechanicalFailures(int limit) {
        String sql = """
                    SELECT TOP (?)
                        Constructor.constructorID,
                        CAST(Constructor.name AS NVARCHAR(255)) AS name,
                        COUNT(Result.resultID) AS mechanicalFailures
                    FROM
                        Result
                    JOIN
                        Constructor ON Result.constructorID = Constructor.constructorID
                    JOIN
                        Status ON Result.statusID = Status.statusID
                    WHERE
                        Status.statusID = 26
                    GROUP BY
                        Constructor.constructorID, Constructor.name
                    ORDER BY
                        mechanicalFailures DESC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Constructors with the Most Mechanical Failures:");
                System.out.printf("%-15s %-30s %-20s%n", "Constructor ID", "Name", "Mechanical Failures");
                System.out.println("----------------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    int constructorID = resultSet.getInt("constructorID");
                    String name = resultSet.getString("name");
                    int mechanicalFailures = resultSet.getInt("mechanicalFailures");

                    System.out.printf("%-15d %-30s %-20d%n", constructorID, name, mechanicalFailures);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //5. Find races with the highest number of accidents and collisions
    public void racesWithHighAccidents(int limit) {
        String sql = """
                    SELECT TOP (?)
                        Race.name,
                        COUNT(Result.resultID) AS numAccidentsAndCollisions
                    FROM
                        Result
                    JOIN
                        Race ON Result.raceID = Race.raceID
                    JOIN
                        Status ON Result.statusID = Status.statusID
                    WHERE
                        Status.statusID = 3 OR Status.statusID = 4
                    GROUP BY
                        Race.raceID, Race.name
                    ORDER BY
                        numAccidentsAndCollisions DESC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Races with the Highest Number of Accidents and Collisions:");
                System.out.printf("%-30s %-20s%n", "Race Name", "Accidents/Collisions");
                System.out.println("--------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String raceName = resultSet.getString("name");
                    int numAccidentsAndCollisions = resultSet.getInt("numAccidentsAndCollisions");

                    System.out.printf("%-30s %-20d%n", raceName, numAccidentsAndCollisions);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //6. Find which constructor performed the fastest average pit stop
    public void fastestAveragePitStop(int limit) {
        String sql = """
                    SELECT TOP (?)
                        Constructor.constructorID,
                        Constructor.name,
                        AVG(PitStop.milliseconds) AS averagePitStop
                    FROM
                        PitStop
                    JOIN
                        DrivesFor ON PitStop.driverID = DrivesFor.driverID
                    JOIN
                        Constructor ON DrivesFor.constructorID = Constructor.constructorID
                    GROUP BY
                        Constructor.constructorID, Constructor.name
                    ORDER BY
                        averagePitStop ASC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Constructors with the Fastest Average Pit Stops:");
                System.out.printf("%-10s %-30s %-20s%n", "Constructor ID", "Constructor Name", "Average Pit Stop (ms)");
                System.out.println("---------------------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    int constructorID = resultSet.getInt("constructorID");
                    String constructorName = resultSet.getString("name");
                    double averagePitStop = resultSet.getDouble("averagePitStop");

                    System.out.printf("%-10d %-30s %-20.2f%n", constructorID, constructorName, averagePitStop);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //7. Find the worst driver in the season
    public void worstDriver() {
        String sql = """
                    SELECT TOP 1
                        Driver.driverID,
                        Driver.forename,
                        Driver.surname,
                        SUM(Result.points) AS totalPoints
                    FROM
                        Result
                    JOIN
                        Driver ON Result.driverID = Driver.driverID
                    GROUP BY
                        Driver.driverID, Driver.forename, Driver.surname
                    ORDER BY
                        totalPoints ASC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Worst Driver in the Season:");
                System.out.printf("%-10s %-15s %-15s %-10s%n", "Driver ID", "Forename", "Surname", "Total Points");
                System.out.println("-------------------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    int driverID = resultSet.getInt("driverID");
                    String forename = resultSet.getString("forename");
                    String surname = resultSet.getString("surname");
                    int totalPoints = resultSet.getInt("totalPoints");

                    System.out.printf("%-10d %-15s %-15s %-10d%n", driverID, forename, surname, totalPoints);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //8.List constructors and their nationalities
    public void listConstructorsAndNationalities() {
        String sql = """
                    SELECT
                        name,
                        nationality
                    FROM
                        Constructor;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("List of Constructors and Their Nationalities:");
                System.out.printf("%-30s %-20s%n", "Constructor Name", "Nationality");
                System.out.println("------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String name = resultSet.getString("name");
                    String nationality = resultSet.getString("nationality");

                    System.out.printf("%-30s %-20s%n", name, nationality);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //9. List all countries where races occurred
    public void listAllCountriesWithRaces() {
        String sql = """
                    SELECT DISTINCT
                        country
                    FROM
                        City
                    ORDER BY
                        country ASC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("List of Countries Associated with Races:");
                System.out.printf("%-30s%n", "Country");
                System.out.println("--------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String country = resultSet.getString("country");

                    System.out.printf("%-30s%n", country);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //10. Display race information
    public void displayRaceInformation() {
        String sql = """
                    SELECT
                        raceID,
                        year,
                        name
                    FROM
                        Race;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Race Information:");
                System.out.printf("%-10s %-10s %-30s%n", "Race ID", "Year", "Race Name");
                System.out.println("---------------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    int raceID = resultSet.getInt("raceID");
                    int year = resultSet.getInt("year");
                    String name = resultSet.getString("name");

                    System.out.printf("%-10d %-10d %-30s%n", raceID, year, name);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //11. circuits in a specific country
    public void listCircuitInCountry( String country) {
        String sql = """
                    SELECT 
                        Circuit.name AS circuit_name, 
                        City.name AS city_name
                    FROM 
                        Circuit
                    INNER JOIN 
                        City ON Circuit.cityID = City.cityID
                    WHERE 
                        City.country = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, country);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Circuits in " + country + ":");
                System.out.printf("%-30s %-30s%n", "Circuit Name", "City");
                System.out.println("----------------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String circuitName = resultSet.getString("circuit_name");
                    String cityName = resultSet.getString("city_name");

                    System.out.printf("%-30s %-30s%n", circuitName, cityName);
                }

                if (!hasResults) {
                    System.out.println("No circuits found in " + country + ".");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //12. Get details of drivers from a specific constructor
    public void driversByConstructor(String constructorName) {
        String sql = """
            SELECT
                Driver.forename,
                Driver.surname,
                Constructor.name
            FROM
                Driver
            INNER JOIN
                DrivesFor ON Driver.driverID = DrivesFor.driverID
            INNER JOIN
                Constructor ON DrivesFor.constructorID = Constructor.constructorID
            WHERE
                CAST(Constructor.name AS NVARCHAR(255)) = ?;
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, constructorName);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Drivers from constructor: " + constructorName);
                System.out.printf("%-20s %-20s %-20s%n", "Forename", "Surname", "Constructor");
                System.out.println("-----------------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String forename = resultSet.getString("forename");
                    String surname = resultSet.getString("surname");
                    String constructor = resultSet.getString("name");

                    System.out.printf("%-20s %-20s %-20s%n", forename, surname, constructor);
                }

                if (!hasResults) {
                    System.out.println("No drivers found for constructor: " + constructorName);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //13. Count the number of races per year
    public void countRacesPerYear() {
        String sql = """
                    SELECT
                        year,
                        COUNT(raceID) AS race_count
                    FROM
                        Race
                    GROUP BY
                        year;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Number of races per year:");
                System.out.printf("%-10s %-10s%n", "Year", "Race Count");
                System.out.println("---------------------------");

                while (resultSet.next()) {
                    int year = resultSet.getInt("year");
                    int raceCount = resultSet.getInt("race_count");

                    System.out.printf("%-10d %-10d%n", year, raceCount);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //14. Fastest lap times in all races
    public void fastestLapTimes(int limit) {
        String sql = """
                    SELECT TOP (?)
                        Driver.forename,
                        Driver.surname,
                        LapTime.time
                    FROM
                        LapTime
                    JOIN
                        Driver ON LapTime.driverID = Driver.driverID
                    ORDER BY
                        LapTime.milliseconds ASC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Fastest Lap Times:");
                System.out.printf("%-20s %-20s %-10s%n", "Forename", "Surname", "Lap Time");
                System.out.println("-----------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String forename = resultSet.getString("forename");
                    String surname = resultSet.getString("surname");
                    String lapTime = resultSet.getString("time");

                    System.out.printf("%-20s %-20s %-10s%n", forename, surname, lapTime);
                }

                if (!hasResults) {
                    System.out.println("No lap times found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //15. Sort constructors by number of wins
    public void constructorsByWins() {
        String sql = """
            SELECT
                CAST(Constructor.name AS NVARCHAR(255)) AS name,
                COUNT(Result.resultID) AS win_count
            FROM
                Result
            INNER JOIN
                Constructor ON Result.constructorID = Constructor.constructorID
            WHERE
                Result.positionOrder = 1
            GROUP BY
                CAST(Constructor.name AS NVARCHAR(255))
            ORDER BY
                win_count DESC;
        """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Constructors by Number of Wins:");
                System.out.printf("%-30s %-10s%n", "Constructor", "Wins");
                System.out.println("--------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String constructorName = resultSet.getString("name");
                    int wins = resultSet.getInt("win_count");

                    System.out.printf("%-30s %-10d%n", constructorName, wins);
                }

                if (!hasResults) {
                    System.out.println("No constructors found with wins.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //16. Top Constructor across all races
    public void topConstructors(int limit) {
        String sql = """
                    SELECT TOP (?) Constructor.name, COUNT(Result.resultID) AS top_finish_count
                    FROM Result
                    INNER JOIN Constructor ON Result.constructorID = Constructor.constructorID
                    WHERE Result.positionOrder <= 3
                    GROUP BY Constructor.name
                    ORDER BY top_finish_count DESC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Top Constructors with Most Top-Three Finishes:");
                System.out.printf("%-30s %-20s%n", "Constructor Name", "Top Finish Count");
                System.out.println("----------------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String constructorName = resultSet.getString("name");
                    int topFinishCount = resultSet.getInt("top_finish_count");

                    System.out.printf("%-30s %-20d%n", constructorName, topFinishCount);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //17. drivers with specific number of wins
    public void findDriversWithSpecificWins( int numWins) {
        String sql = """
                    SELECT Driver.forename, Driver.surname, COUNT(DriverStanding.wins) AS win_count
                    FROM DriverStanding
                    INNER JOIN Driver ON DriverStanding.driverID = Driver.driverID
                    GROUP BY Driver.driverID, Driver.forename, Driver.surname
                    HAVING COUNT(DriverStanding.wins) = ?;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, numWins);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Drivers with " + numWins + " Wins:");
                System.out.printf("%-20s %-20s %-10s%n", "Forename", "Surname", "Wins");
                System.out.println("----------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String forename = resultSet.getString("forename");
                    String surname = resultSet.getString("surname");
                    int wins = resultSet.getInt("win_count");

                    System.out.printf("%-20s %-20s %-10d%n", forename, surname, wins);
                }

                if (!hasResults) {
                    System.out.println("No drivers found with " + numWins + " wins.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //18. compare lap times between 2 drivers in a race
    public void compareLapTimes( int raceID, int driverID1, int driverID2) {
        String sql = """
                    SELECT Driver.forename, Driver.surname, LapTime.time
                    FROM LapTime
                    INNER JOIN Driver ON LapTime.driverID = Driver.driverID
                    WHERE LapTime.raceID = ? AND (Driver.driverID = ? OR Driver.driverID = ?)
                    ORDER BY LapTime.lap;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, raceID);
            statement.setInt(2, driverID1);
            statement.setInt(3, driverID2);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Lap Times Comparison for Race ID: " + raceID);
                System.out.printf("%-20s %-20s %-10s%n", "Forename", "Surname", "Lap Time");
                System.out.println("----------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String forename = resultSet.getString("forename");
                    String surname = resultSet.getString("surname");
                    String lapTime = resultSet.getString("time");

                    System.out.printf("%-20s %-20s %-10s%n", forename, surname, lapTime);
                }

                if (!hasResults) {
                    System.out.println("No lap times found for the selected drivers in this race.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //19. race with the highest average speed for fastest lap
    public void findRaceWithHighestAvgSpeed(int limit) {
        String sql = """
                    SELECT TOP (?)
                        Race.name, AVG(Result.fastestLapSpeed) AS avg_speed
                    FROM Result
                    JOIN Race ON Result.raceID = Race.raceID
                    GROUP BY Race.raceID, Race.name
                    ORDER BY avg_speed DESC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Races with the Highest Average Fastest Lap Speed:");
                System.out.printf("%-30s %-20s%n", "Race Name", "Average Speed (km/h)");
                System.out.println("--------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String raceName = resultSet.getString("name");
                    double avgSpeed = resultSet.getDouble("avg_speed");

                    System.out.printf("%-30s %-20.2f%n", raceName, avgSpeed);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //20. find the total number of pit stops per driver
    public void totalPitStopsPerDriver() {
        String sql = """
                    SELECT Driver.forename, Driver.surname, COUNT(PitStop.stop) AS total_pitstops
                    FROM PitStop
                    INNER JOIN Driver ON PitStop.driverID = Driver.driverID
                    GROUP BY Driver.driverID, Driver.forename, Driver.surname;
                """;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Total Pit Stops Per Driver:");
            System.out.printf("%-20s %-20s %-20s%n", "Driver", "Total Pit Stops");
            System.out.println("---------------------------------------------");

            boolean hasResults = false;
            while (resultSet.next()) {
                hasResults = true;
                String forename = resultSet.getString("forename");
                String surname = resultSet.getString("surname");
                int totalPitStops = resultSet.getInt("total_pitstops");

                System.out.printf("%-20s %-20s %-20d%n", forename, surname, totalPitStops);
            }

            if (!hasResults) {
                System.out.println("No data found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //21. Find races in which a specific driver competed
    public void racesForDriver( int driverID) {
        String sql = """
                    DECLARE @driverID INT = ?; -- (user inputs this)
                    SELECT Race.name, Race.date
                    FROM Result
                    JOIN Race ON Result.raceID = Race.raceID
                    JOIN Driver ON Result.driverID = Driver.driverID
                    WHERE Driver.driverID = @driverID;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, driverID);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Races for Driver ID " + driverID + ":");
                System.out.printf("%-30s %-20s%n", "Race Name", "Race Date");
                System.out.println("--------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String raceName = resultSet.getString("name");
                    Date raceDate = resultSet.getDate("date");

                    System.out.printf("%-30s %-20s%n", raceName, raceDate);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //22. find driver with the most wins
    public void driverWithMostWins(int topDrivers) {
        String sql = "SELECT TOP (?) Driver.forename, Driver.surname, COUNT(*) AS totalWins "
                + "FROM DriverStanding "
                + "INNER JOIN Driver ON DriverStanding.driverID = Driver.driverID "
                + "WHERE DriverStanding.position = 1 "
                + "GROUP BY Driver.driverID, Driver.forename, Driver.surname "
                + "ORDER BY totalWins DESC;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, topDrivers);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Top Drivers with Most Wins:");
                System.out.printf("%-20s %-20s %-20s%n", "Driver", "Total Wins");
                System.out.println("---------------------------------------------");

                boolean hasResults = false;
                while (resultSet.next()) {
                    hasResults = true;
                    String forename = resultSet.getString("forename");
                    String surname = resultSet.getString("surname");
                    int totalWins = resultSet.getInt("totalWins");

                    System.out.printf("%-20s %-20s %-20d%n", forename, surname, totalWins);
                }

                if (!hasResults) {
                    System.out.println("No data found.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //23. Fastest Lap Times For each driver Across All Races
    public void findFastestLapTimesForDriver() {
        String sql = "SELECT raceID, driverID, MIN(milliseconds) AS ms FROM laptime GROUP BY raceID, driverID";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Fastest Lap Times Across All Races:");
            System.out.printf("%-10s %-10s %-15s%n", "Race ID", "Driver ID", "Fastest Lap Time (milliseconds)");
            System.out.println("--------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10d %-10d %-15.2f%n",
                        resultSet.getInt("raceID"),
                        resultSet.getInt("driverID"),
                        resultSet.getDouble("ms"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //24. Average Race Position for Each Driver
    public void findAverageRacePosition() {
        String sql = "SELECT driverID, AVG(positionOrder) AS avgPosition FROM Result GROUP BY driverID ORDER BY avgPosition";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Average Race Position for Each Driver:");
            System.out.printf("%-10s %-15s%n", "Driver ID", "Average Position");
            System.out.println("-----------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10d %-15.2f%n",
                        resultSet.getInt("driverID"),
                        resultSet.getDouble("avgPosition"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //25. Races with the Closest Finish
    public void findClosestFinish(int limit) {
        String sql = "SELECT TOP " + limit + " Race.name, Race.year, " +
                "ABS(res2.milliseconds - res1.milliseconds) AS smallest_gap " +
                "FROM Result res1 " +
                "JOIN Result res2 ON res1.raceID = res2.raceID " +
                "AND res1.positionOrder = 1 AND res2.positionOrder = 2 " +
                "JOIN Race ON res1.raceID = Race.raceID " +
                "ORDER BY smallest_gap ASC;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Races with the Closest Finish:");
                System.out.printf("%-20s %-15s%n", "Race Name", "Smallest Gap");
                System.out.println("------------------------------------");

                while (resultSet.next()) {
                    System.out.printf("%-20s %-15.2f%n", resultSet.getString("name"), resultSet.getDouble("smallest_gap"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //26. Number of Drivers per Constructor
    public void findDriversPerConstructor() {
        String sql = "SELECT constructor.name, COUNT(drivesFor.driverID) AS driver_count " +
                "FROM constructor " +
                "JOIN drivesFor ON constructor.constructorID = drivesFor.constructorID " +
                "GROUP BY constructor.name";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Number of Drivers per Constructor:");
            System.out.printf("%-20s %-15s%n", "Constructor Name", "Driver Count");
            System.out.println("----------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-20s %-15d%n", resultSet.getString("name"), resultSet.getInt("driver_count"));
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //27. Sort Drivers by Total Points Earned
    public void sortDriversByPoints() {
        String sql = "SELECT Driver.forename, Driver.surname, SUM(DriverStanding.points) AS total_points " +
                "FROM DriverStanding JOIN Driver ON DriverStanding.driverID = Driver.driverID " +
                "GROUP BY Driver.driverID, Driver.forename, Driver.surname ORDER BY total_points DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Drivers Sorted by Total Points Earned:");
            System.out.printf("%-15s %-15s %-15s%n", "First Name", "Last Name", "Total Points");
            System.out.println("----------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-15s %-15s %-15.2f%n", resultSet.getString("forename"),
                        resultSet.getString("surname"), resultSet.getDouble("total_points"));
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //28. Driver Who Led the Most Laps in a Race
    public void findDriverLedMostLaps( int raceID, int limit) {
        String sql = "SELECT TOP (?) Driver.forename, Driver.surname, SUM(Result.laps) AS total_laps " +
                "FROM Result INNER JOIN Driver ON Result.driverID = Driver.driverID " +
                "WHERE Result.raceID = ? " +
                "GROUP BY Driver.driverID, Driver.forename, Driver.surname " +
                "ORDER BY total_laps DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.setInt(2, raceID);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Drivers Who Led the Most Laps in a Race:");
                System.out.printf("%-15s %-15s %-15s%n", "First Name", "Last Name", "Total Laps");
                System.out.println("--------------------------------------------");

                while (resultSet.next()) {
                    System.out.printf("%-15s %-15s %-15d%n", resultSet.getString("forename"),
                            resultSet.getString("surname"), resultSet.getInt("total_laps"));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //29. Race Results for a Specific Driver
    public void findRaceResultsByDriver(int driverID) {
        String sql = "SELECT Race.name, Result.positionOrder, Result.points " +
                "FROM Result JOIN Race ON Result.raceID = Race.raceID WHERE Result.driverID = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, driverID);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Race Results for Driver ID: " + driverID);
                System.out.printf("%-30s %-15s %-15s%n", "Race Name", "Position Order", "Points");
                System.out.println("---------------------------------------------------------");

                while (resultSet.next()) {
                    System.out.printf("%-30s %-15d %-15.2f%n", resultSet.getString("name"),
                            resultSet.getInt("positionOrder"), resultSet.getDouble("points"));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //30. Top Drivers in a Specific Race
    public void findTopDriversInRace(int raceID, int limit) {
        String sql = "SELECT TOP (?) Driver.forename, Driver.surname, Result.positionOrder, Result.points " +
                "FROM Result JOIN Driver ON Result.driverID = Driver.driverID " +
                "WHERE Result.raceID = ? ORDER BY Result.positionOrder ASC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            statement.setInt(2, raceID);

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Top Drivers in Race ID: " + raceID);
                System.out.printf("%-15s %-15s %-15s %-15s%n", "First Name", "Last Name", "Position Order", "Points");
                System.out.println("---------------------------------------------------------------");

                while (resultSet.next()) {
                    System.out.printf("%-15s %-15s %-15d %-15.2f%n", resultSet.getString("forename"),
                            resultSet.getString("surname"), resultSet.getInt("positionOrder"), resultSet.getDouble("points"));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //31. Constructor with the Highest Points in a Season
    public void findConstructorHighestPoints(int limit) {
        String sql = "SELECT TOP " + limit + "CAST(Constructor.name AS NVARCHAR(MAX)) AS name, " +
                "SUM(ConstructorStanding.points) AS totalPoints " +
                "FROM ConstructorStanding " +
                "INNER JOIN Constructor ON ConstructorStanding.constructorID = Constructor.constructorID " +
                "GROUP BY Constructor.constructorID, CAST(Constructor.name AS NVARCHAR(MAX)) " +
                "ORDER BY totalPoints DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Constructors with Highest Points:");
                System.out.printf("%-20s %-15s%n", "Constructor Name", "Total Points");
                System.out.println("-----------------------------------");

                while (resultSet.next()) {
                    System.out.printf("%-20s %-15.2f%n", resultSet.getString("name"), resultSet.getDouble("totalPoints"));
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //32. Find races with below-average participation
    public void findRacesWithBelowAverageParticipation() {
        String sql = """
                SELECT Race.raceID, Race.name, Race.date, COUNT(Result.driverID) AS participant_count
                FROM Race
                JOIN Result ON Race.raceID = Result.raceID
                GROUP BY Race.raceID, Race.name, Race.date
                HAVING participant_count < (
                    SELECT AVG(total_participants)
                    FROM (
                        SELECT COUNT(driverID) AS total_participants
                        FROM Result
                        GROUP BY raceID
                    ) AS race_participation
                )
                ORDER BY participant_count ASC;
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Races With Below-Average Participation:");
            System.out.printf("%-10s %-25s %-15s %-15s%n", "Race ID", "Race Name", "Date", "Participants");
            System.out.println("---------------------------------------------------------------");

            while (resultSet.next()) {
                int raceID = resultSet.getInt("raceID");
                String raceName = resultSet.getString("name");
                Date raceDate = resultSet.getDate("date");
                int participantCount = resultSet.getInt("participant_count");

                String formattedDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(raceDate);

                System.out.printf("%-10d %-25s %-15s %-15d%n", raceID, raceName, formattedDate, participantCount);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching races with below-average participation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //33. Identify drivers who have driven for multiple constructors
    public void findDriversWithMultipleConstructors() {
        String sql = """
                SELECT Driver.forename, Driver.surname
                FROM Driver
                WHERE driverID IN (
                    SELECT driverID
                    FROM DrivesFor
                    GROUP BY driverID
                    HAVING COUNT(DISTINCT constructorID) > 1
                );
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("Drivers Who Have Driven for Multiple Constructors:");
            System.out.printf("%-15s %-15s%n", "First Name", "Last Name");
            System.out.println("---------------------------------------");

            while (resultSet.next()) {
                String forename = resultSet.getString("forename");
                String surname = resultSet.getString("surname");

                System.out.printf("%-15s %-15s%n", forename, surname);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching drivers with multiple constructors: " + e.getMessage());
            e.printStackTrace();
        }
    }

}


