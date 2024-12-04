import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Populate {
    private String file;
    private String connectionUrl;

    public static void main(String[] args) {
        Populate populate = new Populate();
        populate.loadConfigAndPopulate();
    }

    public void loadConfigAndPopulate() {
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

//        constructor();
//        constructorResult();
//        circuit();
//        race();
//        driver();
//        status();
//
//        constructorStanding();
//        qualifyingRecord();
//        driver();
//        LapTime();



    }

    public void constructor() {
        String sql = "INSERT INTO constructor (constructorRef, name, nationality) VALUES (?, ?, ?)";
        file = "csv_files/constructors.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                String constructorRef = fields[1].trim();
                String name = fields[2].trim();
                String nationality = fields[3].trim();

                preparedStatement.setString(1, constructorRef);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, nationality);

                preparedStatement.executeUpdate();
            }

            System.out.println("constructor table successfully populated");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }


    public void constructorResult() {
        String sql = "INSERT INTO constructorResults (raceID,constructorID,points,status) VALUES (?, ?,?,?)";
        file = "csv_files/constructor_results.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int raceID = Integer.parseInt(fields[1].trim());
                int constructorID = Integer.parseInt(fields[2].trim());
                int points = Integer.parseInt(fields[3].trim());
                String status = fields[4].trim();

                preparedStatement.setInt(1, raceID);
                preparedStatement.setInt(2, constructorID);
                preparedStatement.setInt(3, points);


                if (!status.equals("\\N") && !status.isEmpty()) {
                    preparedStatement.setString(4, status);
                } else {
                    preparedStatement.setNull(4, Types.VARCHAR);
                }

                preparedStatement.executeUpdate();
            }


            System.out.println("constructor_Results table successfully populated");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    public void constructorStanding() {
        String sql = "INSERT INTO constructorStanding (raceID,wins,points,position,constructorID) VALUES (?, ?,?,?,?)";
        file = "csv_files/constructor_standings.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int raceID = Integer.parseInt(fields[1].trim());
                int wins = Integer.parseInt(fields[2].trim());
                int points = Integer.parseInt(fields[3].trim());
                int position = Integer.parseInt(fields[4].trim());
                int constructorID = Integer.parseInt(fields[5].trim());

                preparedStatement.setInt(1, raceID);
                preparedStatement.setInt(2, wins);
                preparedStatement.setInt(3, points);
                preparedStatement.setInt(4, position);
                preparedStatement.setInt(5, constructorID);

                preparedStatement.executeUpdate();
            }


            System.out.println("constructor_Results table successfully populated");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }


    public void qualifyingRecord() {
        String sql = "INSERT INTO qualifyingRecord (raceID, driverID, constructorID, number, position, q1, q2, q3) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        file = "csv_files/qualifying.csv";

        System.out.println("tryign");
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine(); // Skip the header row

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int raceID = Integer.parseInt(fields[1].trim());
                int driverID = Integer.parseInt(fields[2].trim());
                int constructorID = Integer.parseInt(fields[3].trim());
                int number = Integer.parseInt(fields[4].trim());
                int position = Integer.parseInt(fields[5].trim());

                // Parse q1, q2, q3 or set them as NULL if `\N`
                Double q1 = fields[6].trim().equals("\\N") ? null : parseTimeToDecimal(fields[6].trim());
                Double q2 = fields[7].trim().equals("\\N") ? null : parseTimeToDecimal(fields[7].trim());
                Double q3 = fields[8].trim().equals("\\N") ? null : parseTimeToDecimal(fields[8].trim());

                preparedStatement.setInt(1, raceID);
                preparedStatement.setInt(2, driverID);
                preparedStatement.setInt(3, constructorID);
                preparedStatement.setInt(4, number);
                preparedStatement.setInt(5, position);

                // Use `setNull` for null values, or `setDouble` for non-null
                if (q1 == null) {
                    preparedStatement.setNull(6, java.sql.Types.DECIMAL);
                } else {
                    preparedStatement.setDouble(6, q1);
                }
                if (q2 == null) {
                    preparedStatement.setNull(7, java.sql.Types.DECIMAL);
                } else {
                    preparedStatement.setDouble(7, q2);
                }
                if (q3 == null) {
                    preparedStatement.setNull(8, java.sql.Types.DECIMAL);
                } else {
                    preparedStatement.setDouble(8, q3);
                }

                preparedStatement.executeUpdate();
                System.out.println("tryign");
            }

            System.out.println("qualifyingRecord table successfully populated");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    // Helper method to convert time (hh:mm:ss.s) to DECIMAL
    private Double parseTimeToDecimal(String time) {
        try {
            String[] parts = time.split(":");
            double minutes = Double.parseDouble(parts[0]) * 60; // Convert hours to seconds
            double seconds = Double.parseDouble(parts[1]);
            return minutes + seconds;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format: " + time, e);
        }
    }




    public void circuit() {
        String sql = "INSERT INTO circuit (cityID, circuitRef, name, long, lat, altitude) VALUES (?, ?, ?, ?, ?, ?)";
        file = "csv_files/circuits.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int cityID = Integer.parseInt(fields[9].trim());
                String circuitRef = fields[1].trim();
                String name = fields[2].trim();
                double longitude = Double.parseDouble(fields[5].trim());
                double latitude = Double.parseDouble(fields[6].trim());
                int altitude = Integer.parseInt(fields[7].trim());

                preparedStatement.setInt(1, cityID);
                preparedStatement.setString(2, circuitRef);
                preparedStatement.setString(3, name);
                preparedStatement.setDouble(4, longitude);
                preparedStatement.setDouble(5, latitude);
                preparedStatement.setInt(6, altitude);

                preparedStatement.executeUpdate();
            }

            System.out.println("Circuit table successfully populated!");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }



    public void race() {
        String sql = "INSERT INTO race (year, round, circuitID, name, date, time) VALUES (?, ?, ?, ?, ?, ?)";
        file = "csv_files/races.csv";

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int year = Integer.parseInt(fields[1].trim());
                int round = Integer.parseInt(fields[2].trim());
                int circuitID = Integer.parseInt(fields[3].trim());
                String name = fields[4].trim();
                String date = fields[5].trim();
                String time = fields[6].trim();

                preparedStatement.setInt(1, year);
                preparedStatement.setInt(2, round);
                preparedStatement.setInt(3, circuitID);
                preparedStatement.setString(4, name);

                // Use helper methods for date and time parsing
                java.sql.Date sqlDate = parseDate(date);
                java.sql.Time sqlTime = parseTime(time);

                if (sqlDate != null) {
                    preparedStatement.setDate(5, sqlDate);
                } else {
                    preparedStatement.setNull(5, java.sql.Types.DATE);
                }

                if (sqlTime != null) {
                    preparedStatement.setTime(6, sqlTime);
                } else {
                    preparedStatement.setNull(6, java.sql.Types.TIME);
                }

                preparedStatement.executeUpdate();
            }

            System.out.println("races table successfully populated");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    // Helper method to parse date
    private java.sql.Date parseDate(String date) {
        try {
            // Remove quotes and trim the input
            date = date.replace("\"", "").trim();
            // Return null for missing or invalid dates
            return date.equals("\\N") || date.isEmpty() ? null : java.sql.Date.valueOf(date);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

    // Helper method to parse time
    private java.sql.Time parseTime(String time) {
        try {
            // Remove quotes and trim the input
            time = time.replace("\"", "").trim();
            // Return null for missing or invalid times
            return time.equals("\\N") || time.isEmpty() ? null : java.sql.Time.valueOf(time);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid time format: " + time, e);
        }
    }




    public void sprintResult() {
        String sql = "INSERT INTO sprintResult (raceID, driverID, constructorID, number, grid, position, positionOrder, points, laps, time, milliseconds, fastestLap, fastestLapTime, statusID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        file = "csv_files/sprint_results.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");


                int raceID = Integer.parseInt(fields[1].trim());
                int driverID = Integer.parseInt(fields[2].trim());
                int constructorID = Integer.parseInt(fields[3].trim());
                int number = Integer.parseInt(fields[4].trim());
                int grid = Integer.parseInt(fields[5].trim());
                Integer position = fields[6].trim().equals("\\N") ? null : Integer.parseInt(fields[6].trim());
                int positionOrder = Integer.parseInt(fields[8].trim());
                int points = Integer.parseInt(fields[9].trim());
                int laps = Integer.parseInt(fields[10].trim());
                String time = fields[11].trim().equals("\\N") ? null : fields[10].trim();
                Integer milliseconds = fields[12].trim().equals("\\N") ? null : Integer.parseInt(fields[12].trim());
                Integer fastestLap = fields[13].trim().equals("\\N") ? null : Integer.parseInt(fields[13].trim());
                String fastestLapTime = fields[14].trim().equals("\\N") ? null : fields[14].trim();
                int statusID = Integer.parseInt(fields[15].trim());


                preparedStatement.setInt(1, raceID);
                preparedStatement.setInt(2, driverID);
                preparedStatement.setInt(3, constructorID);
                preparedStatement.setInt(4, number);
                preparedStatement.setInt(5, grid);

                if (position != null) {
                    preparedStatement.setInt(6, position);
                } else {
                    preparedStatement.setNull(6, Types.INTEGER);
                }

                preparedStatement.setInt(7, positionOrder);
                preparedStatement.setInt(8, points);
                preparedStatement.setInt(9, laps);
                if (time != null) {
                    preparedStatement.setString(10, time);
                } else {
                    preparedStatement.setNull(10, Types.VARCHAR);
                }

                if (milliseconds != null) {
                    preparedStatement.setInt(11, milliseconds);
                } else {
                    preparedStatement.setNull(11, Types.INTEGER);
                }

                if (fastestLap != null) {
                    preparedStatement.setInt(12, fastestLap);
                } else {
                    preparedStatement.setNull(12, Types.INTEGER);
                }

                if (fastestLapTime != null) {
                    preparedStatement.setString(13, fastestLapTime);
                } else {
                    preparedStatement.setNull(13, Types.VARCHAR);
                }

                preparedStatement.setInt(14, statusID);

                preparedStatement.executeUpdate();
            }

            System.out.println("sprintResult table successfully populated");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
        }
    }

    private void driver() {
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            BufferedReader reader = new BufferedReader(new FileReader("csv_files/drivers.csv"));
            reader.readLine(); // Skip header
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO driver (driverRef, forename, surname,number,nationality, code, dob) VALUES (?, ?, ?, ?,?,?,?)");
                stmt.setString(1, columns[1].trim());
                stmt.setString(2, columns[2].trim());
                stmt.setString(3, columns[3].trim());

                Integer number = columns[2].trim().equals("\\N") ? null : Integer.parseInt(columns[2].trim());

                if (number != null) {
                    stmt.setInt(4, number);
                } else {
                    stmt.setNull(4, Types.INTEGER);
                }

                stmt.setString(5, columns[7].trim());
                stmt.setString(6, columns[3].trim());
                stmt.setDate(7, Date.valueOf(columns[6]));

                stmt.executeUpdate();
                stmt.close();


            }
            reader.close();
            System.out.println("driver table successfully populated");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void status() {
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            BufferedReader reader = new BufferedReader(new FileReader("csv_files/status.csv"));
            reader.readLine(); // Skip header
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO status (status) VALUES (?)");
                stmt.setString(1, columns[1]);
                stmt.executeUpdate();
                stmt.close();
            }
            reader.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private String parseTime1(String lapTime) throws ParseException {
        // Remove surrounding double quotes if they exist
        lapTime = lapTime.replace("\"", "").trim();

        // Format is expected as "m:ss.SSS"
        SimpleDateFormat inputFormat = new SimpleDateFormat("m:ss.SSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        // Parse input time and reformat
        return outputFormat.format(inputFormat.parse(lapTime));
    }

    // Insert data into 'laptime' table from CSV file
    private void LapTime() {
    try (Connection connection = DriverManager.getConnection(connectionUrl)) {
        BufferedReader reader = new BufferedReader(new FileReader("csv_files/lap_times.csv"));
        reader.readLine(); // Skip header

        String line;
        while ((line = reader.readLine()) != null) {
            String[] columns = line.split(",");
            PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO laptime (raceID, driverID, lap, position, time, milliseconds) VALUES (?, ?, ?, ?, ?, ?)"
            );

            stmt.setInt(1, Integer.parseInt(columns[0].trim())); // raceID
            stmt.setInt(2, Integer.parseInt(columns[1].trim())); // driverID
            stmt.setInt(3, Integer.parseInt(columns[2].trim())); // lap
            stmt.setInt(4, Integer.parseInt(columns[3].trim())); // position

            // Parse and set time
            String formattedTime = parseTime1(columns[4].trim()).replace("00:", "");
            stmt.setString(5, formattedTime);

            stmt.setInt(6, Integer.parseInt(columns[5].trim())); // milliseconds
            stmt.executeUpdate();
            stmt.close();
        }
        reader.close();
    } catch (IOException | SQLException | ParseException e) {
        e.printStackTrace();
    }
}

    public static String parseTime2(String timeString) throws ParseException {
        try {
            // Define the formatter for HH:mm:ss
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime time = LocalTime.parse(timeString, formatter);
            return time.toString(); // Converts to ISO-8601 (HH:mm:ss) string
        } catch (Exception e) {
            throw new ParseException("Unable to parse time: " + timeString, 0);
        }
    }

    // Insert data into 'pitstop' table from CSV file
    private void Pitstop() {
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            BufferedReader reader = new BufferedReader(new FileReader("csv_files/pit_stops.csv"));
            reader.readLine(); // Skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO pitstop (raceID, driverID, stop, lap, time, duration, milliseconds) VALUES (?, ?, ?, ?, ?, ?, ?)"
                );
                stmt.setInt(1, Integer.parseInt(columns[0].trim())); // raceID
                stmt.setInt(2, Integer.parseInt(columns[1].trim())); // driverID
                stmt.setInt(3, Integer.parseInt(columns[2].trim())); // stop
                stmt.setInt(4, Integer.parseInt(columns[3].trim())); // lap
                String formattedTime = parseTime2(columns[4].trim()); // Parse HH:mm:ss to a string
                stmt.setString(5, formattedTime);
                stmt.setDouble(6, Double.parseDouble(columns[5].trim())); // duration
                stmt.setInt(7, Integer.parseInt(columns[6].trim())); // milliseconds
                stmt.executeUpdate();
                stmt.close();
            }
            reader.close();
        } catch (IOException | SQLException | ParseException e) {
            e.printStackTrace();
        }
    }


    /**
    // Insert data into 'result' table from CSV file
    private void insertResultData() {
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            BufferedReader reader = new BufferedReader(new FileReader("result.csv"));
            reader.readLine(); // Skip header
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO result (raceID, driverID, positionOrder, points, timeGap) VALUES (?, ?, ?, ?, ?)");
                stmt.setInt(1, Integer.parseInt(columns[0]));
                stmt.setInt(2, Integer.parseInt(columns[1]));
                stmt.setInt(3, Integer.parseInt(columns[2]));
                stmt.setInt(4, Integer.parseInt(columns[3]));
                stmt.setDouble(5, Double.parseDouble(columns[4]));
                stmt.executeUpdate();
                stmt.close();
            }
            reader.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
        
    // Insert data into 'records' table from CSV file
    private void insertRecordsData() {
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            BufferedReader reader = new BufferedReader(new FileReader("records.csv"));
            reader.readLine(); // Skip header
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO records (raceID, driverID, recordType, value) VALUES (?, ?, ?, ?)");
                stmt.setInt(1, Integer.parseInt(columns[0]));
                stmt.setInt(2, Integer.parseInt(columns[1]));
                stmt.setString(3, columns[2]);
                stmt.setString(4, columns[3]);
                stmt.executeUpdate();
                stmt.close();
            }
            reader.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
    **/
    // Chuka table inserts end here


}
