import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Database {
   private String connectionUrl;

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

       connectionUrl = "jdbc:sqlserver://uranium.cs.umanitoba.ca:1433;"
               + "database=cs3380;"
               + "user=" + username + ";"
               + "password=" + password + ";"
               + "encrypt=false;"
               + "trustServerCertificate=false;"
               + "loginTimeout=30;";

   }

   //Queries
    //How many races did each driver win
    public void racesDriverWon(Connection connection) {
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

//How many drivers were disqualified in the season
public void driversDisqualified(Connection connection, int year) {
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

//Which driver improved the most throughout the season
public void improvedDriver(Connection connection) {
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

//Constructors who have the most mechanical failures
public void constructorMostMechanicalFailures(Connection connection, int limit) {
    String sql = """
        SELECT TOP (?)
            Constructor.constructorID,
            Constructor.name,
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

//Find races with the highest number of accidents and collisions
public void racesWithHighAccidents(Connection connection, int limit) {
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




   //Queries 23-33 by Chuka
   //23. Fastest Lap Times Across All Races
   public void findFastestLapTimes(Connection connection) {
       try {
           String sql = "SELECT raceID, driverID, MIN(fastestLapTime) AS fastestLapTime FROM LapTime GROUP BY raceID, driverID";
           PreparedStatement statement = connection.prepareStatement(sql);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Fastest Lap Times Across All Races:");
           System.out.printf("%-10s %-10s %-15s%n", "Race ID", "Driver ID", "Fastest Lap Time");
           System.out.println("--------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-10d %-10d %-15.2f%n", resultSet.getInt("raceID"),
                       resultSet.getInt("driverID"), resultSet.getDouble("fastestLapTime"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //24. Average Race Position for Each Driver
   public void findAverageRacePosition(Connection connection) {
       try {
           String sql = "SELECT driverID, AVG(positionOrder) AS avgPosition FROM Result GROUP BY driverID ORDER BY avgPosition";
           PreparedStatement statement = connection.prepareStatement(sql);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Average Race Position for Each Driver:");
           System.out.printf("%-10s %-15s%n", "Driver ID", "Average Position");
           System.out.println("-----------------------------");
           while (resultSet.next()) {
               System.out.printf("%-10d %-15.2f%n", resultSet.getInt("driverID"), resultSet.getDouble("avgPosition"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //25. Races with the Closest Finish
   public void findClosestFinish(Connection connection, int limit) {
       try {
           String sql = "SELECT TOP (?) Race.name, MIN(Result.timeGap) AS smallest_gap " +
                   "FROM Result JOIN Race ON Result.raceID = Race.raceID " +
                   "WHERE Result.positionOrder = 1 OR Result.positionOrder = 2 " +
                   "GROUP BY Race.name ORDER BY smallest_gap ASC";
           PreparedStatement statement = connection.prepareStatement(sql);
           statement.setInt(1, limit);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Races with the Closest Finish:");
           System.out.printf("%-20s %-15s%n", "Race Name", "Smallest Gap");
           System.out.println("------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-20s %-15.2f%n", resultSet.getString("name"), resultSet.getDouble("smallest_gap"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //26. Number of Drivers per Constructor
   public void findDriversPerConstructor(Connection connection) {
       try {
           String sql = "SELECT Constructor.name, COUNT(DrivesFor.driverID) AS driver_count " +
                   "FROM Constructor JOIN DrivesFor ON Constructor.constructorID = DrivesFor.constructorID " +
                   "GROUP BY Constructor.name";
           PreparedStatement statement = connection.prepareStatement(sql);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Number of Drivers per Constructor:");
           System.out.printf("%-20s %-15s%n", "Constructor Name", "Driver Count");
           System.out.println("----------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-20s %-15d%n", resultSet.getString("name"), resultSet.getInt("driver_count"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //27. Sort Drivers by Total Points Earned
   public void sortDriversByPoints(Connection connection) {
       try {
           String sql = "SELECT Driver.forename, Driver.surname, SUM(DriverStanding.points) AS total_points " +
                   "FROM DriverStanding JOIN Driver ON DriverStanding.driverID = Driver.driverID " +
                   "GROUP BY Driver.driverID ORDER BY total_points DESC";
           PreparedStatement statement = connection.prepareStatement(sql);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Drivers Sorted by Total Points Earned:");
           System.out.printf("%-15s %-15s %-15s%n", "First Name", "Last Name", "Total Points");
           System.out.println("----------------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-15s %-15s %-15.2f%n", resultSet.getString("forename"),
                       resultSet.getString("surname"), resultSet.getDouble("total_points"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //28. Driver Who Led the Most Laps in a Race
   public void findDriverLedMostLaps(Connection connection, int raceID, int limit) {
       try {
           String sql = "SELECT TOP (?) Driver.forename, Driver.surname, SUM(Result.laps) AS total_laps " +
                   "FROM Result INNER JOIN Driver ON Result.driverID = Driver.driverID " +
                   "WHERE Result.raceID = ? GROUP BY Driver.driverID, Driver.forename, Driver.surname " +
                   "ORDER BY total_laps DESC";
           PreparedStatement statement = connection.prepareStatement(sql);
           statement.setInt(1, limit);
           statement.setInt(2, raceID);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Drivers Who Led the Most Laps in a Race:");
           System.out.printf("%-15s %-15s %-15s%n", "First Name", "Last Name", "Total Laps");
           System.out.println("--------------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-15s %-15s %-15d%n", resultSet.getString("forename"),
                       resultSet.getString("surname"), resultSet.getInt("total_laps"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //29. Race Results for a Specific Driver
   public void findRaceResultsByDriver(Connection connection, int driverID) {
       try {
           String sql = "SELECT Race.name, Result.positionOrder, Result.points " +
                   "FROM Result JOIN Race ON Result.raceID = Race.raceID WHERE Result.driverID = ?";
           PreparedStatement statement = connection.prepareStatement(sql);
           statement.setInt(1, driverID);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Race Results for Driver ID: " + driverID);
           System.out.printf("%-20s %-15s %-15s%n", "Race Name", "Position Order", "Points");
           System.out.println("----------------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-20s %-15d %-15.2f%n", resultSet.getString("name"),
                       resultSet.getInt("positionOrder"), resultSet.getDouble("points"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //30. Top Drivers in a Specific Race
   public void findTopDriversInRace(Connection connection, int raceID, int limit) {
       try {
           String sql = "SELECT TOP (?) Driver.forename, Driver.surname, Result.positionOrder, Result.points " +
                   "FROM Result JOIN Driver ON Result.driverID = Driver.driverID " +
                   "WHERE Result.raceID = ? ORDER BY Result.positionOrder ASC";
           PreparedStatement statement = connection.prepareStatement(sql);
           statement.setInt(1, limit);
           statement.setInt(2, raceID);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Top Drivers in Race ID: " + raceID);
           System.out.printf("%-15s %-15s %-15s %-15s%n", "First Name", "Last Name", "Position Order", "Points");
           System.out.println("---------------------------------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-15s %-15s %-15d %-15.2f%n", resultSet.getString("forename"),
                       resultSet.getString("surname"), resultSet.getInt("positionOrder"), resultSet.getDouble("points"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //31. Constructor with the Highest Points in a Season
   public void findConstructorHighestPoints(Connection connection, int limit) {
       try {
           String sql = "SELECT TOP (?) Constructor.name, SUM(ConstructorStanding.points) AS totalPoints " +
                   "FROM ConstructorStanding INNER JOIN Constructor ON ConstructorStanding.constructorID = Constructor.constructorID " +
                   "GROUP BY Constructor.constructorID, Constructor.name ORDER BY totalPoints DESC";
           PreparedStatement statement = connection.prepareStatement(sql);
           statement.setInt(1, limit);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Constructors with Highest Points:");
           System.out.printf("%-20s %-15s%n", "Constructor Name", "Total Points");
           System.out.println("-----------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-20s %-15.2f%n", resultSet.getString("name"), resultSet.getDouble("totalPoints"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //32. Find races with below-average participation
   public void findRacesWithBelowAverageParticipation(Connection connection) {
       try {
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
           PreparedStatement statement = connection.prepareStatement(sql);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Races With Below-Average Participation:");
           System.out.printf("%-10s %-25s %-15s %-15s%n", "Race ID", "Race Name", "Date", "Participants");
           System.out.println("---------------------------------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-10d %-25s %-15s %-15d%n",
                       resultSet.getInt("raceID"),
                       resultSet.getString("name"),
                       resultSet.getDate("date"),
                       resultSet.getInt("participant_count"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

   //33. Identify drivers who have driven for multiple constructors
   public void findDriversWithMultipleConstructors(Connection connection) {
       try {
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
           PreparedStatement statement = connection.prepareStatement(sql);
           ResultSet resultSet = statement.executeQuery();

           System.out.println("Drivers Who Have Driven for Multiple Constructors:");
           System.out.printf("%-15s %-15s%n", "First Name", "Last Name");
           System.out.println("---------------------------------------");
           while (resultSet.next()) {
               System.out.printf("%-15s %-15s%n", resultSet.getString("forename"), resultSet.getString("surname"));
           }
           resultSet.close();
           statement.close();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }


}


