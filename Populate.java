import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
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

        System.out.println("Tables are being populated, might take while...");
        city();
        circuit();
        race();
        constructor();
        driver();
        result();
        status();
        sprintResult();
        driverStanding();
        constructorResult();
        constructorStanding();
        Pitstop();
        qualifyingRecord();
        LapTime();
        drivesFor();
        compete();


    }

    public void city() {
        String sql = "INSERT INTO city (name, country) VALUES (?, ?)";
        file = "csv_files/city.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                String name = fields[1].trim();
                String country = fields[2].trim();


                preparedStatement.setString(1, name);
                preparedStatement.setString(2, country);

                preparedStatement.executeUpdate();
            }
            System.out.println("City table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    public void circuit() {
        String sql = "INSERT INTO circuit (circuitID, cityID, circuitRef, name, long, lat, altitude) VALUES (?,?, ?, ?, ?, ?, ?)";
        file = "csv_files/circuits.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int circuitID = Integer.parseInt(fields[0].trim());
                int cityID = Integer.parseInt(fields[1].trim());
                String circuitRef = fields[2].trim();
                String name = fields[3].trim();
                double longitude = Double.parseDouble(fields[6].trim());
                double latitude = Double.parseDouble(fields[7].trim());
                int altitude = Integer.parseInt(fields[8].trim());

                preparedStatement.setInt(1, circuitID);
                preparedStatement.setInt(2, cityID);
                preparedStatement.setString(3, circuitRef);
                preparedStatement.setString(4, name);
                preparedStatement.setDouble(5, longitude);
                preparedStatement.setDouble(6, latitude);
                preparedStatement.setInt(7, altitude);

                preparedStatement.executeUpdate();
            }
            System.out.println("Circuit table populated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    public void race() {
        String sql = "INSERT INTO race (raceID, year, round, circuitID, name, date, time) VALUES (?,?, ?, ?, ?, ?, ?)";
        file = "csv_files/races.csv";

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int raceID = Integer.parseInt(fields[0].trim());
                int year = Integer.parseInt(fields[1].trim());
                int round = Integer.parseInt(fields[2].trim());
                int circuitID = Integer.parseInt(fields[3].trim());
                String name = fields[4].trim();
                String date = fields[5].trim();
                String time = fields[6].trim();

                preparedStatement.setInt(1, raceID);
                preparedStatement.setInt(2, year);
                preparedStatement.setInt(3, round);
                preparedStatement.setInt(4, circuitID);
                preparedStatement.setString(5, name);

                java.sql.Date sqlDate = parseDate(date);
                java.sql.Time sqlTime = parseTime(time);

                if (sqlDate != null) {
                    preparedStatement.setDate(6, sqlDate);
                } else {
                    preparedStatement.setNull(6, java.sql.Types.DATE);
                }

                if (sqlTime != null) {
                    preparedStatement.setTime(7, sqlTime);
                } else {
                    preparedStatement.setNull(7, java.sql.Types.TIME);
                }

                preparedStatement.executeUpdate();
            }
            System.out.println("Race table populated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    public void constructor() {
        String sql = "INSERT INTO constructor (constructorID, constructorRef, name, nationality) VALUES (?,?, ?, ?)";
        file = "csv_files/constructors.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int constructorID = Integer.parseInt(fields[0].trim());
                String constructorRef = fields[1].trim();
                String name = fields[2].trim();
                String nationality = fields[3].trim();

                preparedStatement.setInt(1, constructorID);
                preparedStatement.setString(2, constructorRef);
                preparedStatement.setString(3, name);
                preparedStatement.setString(4, nationality);

                preparedStatement.executeUpdate();
            }

            System.out.println("Constructor table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    private void driver() {
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            BufferedReader reader = new BufferedReader(new FileReader("csv_files/drivers.csv"));
            reader.readLine(); // Skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO driver (driverID, driverRef, forename, surname,number,nationality, code, dob) VALUES (?,?, ?, ?, ?,?,?,?)");

                stmt.setInt(1, Integer.parseInt(columns[0].trim()));
                stmt.setString(2, columns[1].trim());
                stmt.setString(3, columns[4].trim());
                stmt.setString(4, columns[5].trim());

                Integer number = columns[2].trim().equals("\\N") ? null : Integer.parseInt(columns[2].trim());

                if (number != null) {
                    stmt.setInt(5, number);
                } else {
                    stmt.setNull(5, Types.INTEGER);
                }

                stmt.setString(6, columns[7].trim());
                stmt.setString(7, columns[3].trim());
                stmt.setDate(8, Date.valueOf(columns[6]));
                stmt.executeUpdate();
                stmt.close();
            }
            reader.close();
            System.out.println("Driver table populated successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void result() {
        String sql = "INSERT INTO result (resultID, raceID, driverID, constructorID, number, grid, positionOrder, points, laps, time, milliseconds, fastestLap, rank, fastestLapTime, fastestLapSpeed, statusID) VALUES (?,?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String file = "csv_files/results.csv";

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int resultID = Integer.parseInt(fields[0].trim());
                int raceID = Integer.parseInt(fields[1].trim());
                int driverID = Integer.parseInt(fields[2].trim());
                int constructorID = Integer.parseInt(fields[3].trim());
                Integer number = fields[4].trim().equals("\\N") ? null : Integer.parseInt(fields[4].trim());
                int grid = Integer.parseInt(fields[5].trim());
                int positionOrder = Integer.parseInt(fields[8].trim());
                BigDecimal points = new BigDecimal(fields[9].trim());
                int laps = Integer.parseInt(fields[10].trim());

                String time = fields[11].trim().equals("\\N") ? null : fields[11].trim();
                Integer milliseconds = fields[12].trim().equals("\\N") ? null : Integer.parseInt(fields[12].trim());
                Integer fastestLap = fields[13].trim().equals("\\N") ? null : Integer.parseInt(fields[13].trim());
                Integer rank = fields[14].trim().equals("\\N") ? null : Integer.parseInt(fields[14].trim());
                String fastestLapTime = fields[15].trim().equals("\\N") ? null : fields[15].trim();

                String fastestLapSpeedStr = fields[16].trim();
                BigDecimal fastestLapSpeed = null;
                if (!fastestLapSpeedStr.equals("\\N")) {

                    fastestLapSpeedStr = fastestLapSpeedStr.replaceAll("\"", "").trim();
                    try {
                        fastestLapSpeed = new BigDecimal(fastestLapSpeedStr);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid fastestLapSpeed value: " + fastestLapSpeedStr);
                        fastestLapSpeed = null;
                    }
                }

                int statusID = Integer.parseInt(fields[17].trim());

                preparedStatement.setInt(1, resultID);
                preparedStatement.setInt(2, raceID);
                preparedStatement.setInt(3, driverID);
                preparedStatement.setInt(4, constructorID);
                preparedStatement.setInt(6, grid);
                preparedStatement.setInt(7, positionOrder);
                preparedStatement.setBigDecimal(8, points);
                preparedStatement.setInt(9, laps);

                if (number != null) {
                    preparedStatement.setInt(5, number);
                } else {
                    preparedStatement.setNull(5, Types.INTEGER);
                }

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

                if (rank != null) {
                    preparedStatement.setInt(13, rank);
                } else {
                    preparedStatement.setNull(13, Types.INTEGER);
                }

                if (fastestLapTime != null) {
                    preparedStatement.setString(14, fastestLapTime);
                } else {
                    preparedStatement.setNull(14, Types.VARCHAR);
                }

                if (fastestLapSpeed != null) {
                    preparedStatement.setBigDecimal(15, fastestLapSpeed);
                } else {
                    preparedStatement.setNull(15, Types.DECIMAL);  // Correct type for DECIMAL column
                }

                preparedStatement.setInt(16, statusID);

                preparedStatement.executeUpdate();
            }

            System.out.println("Result table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
        }
    }

    private void status() {
        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            BufferedReader reader = new BufferedReader(new FileReader("csv_files/status.csv"));
            reader.readLine(); // Skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO status (statusID, status) VALUES (?,?)");
                stmt.setInt(1, Integer.parseInt(columns[0].trim()));
                stmt.setString(2, columns[1].trim());
                stmt.executeUpdate();
                stmt.close();
            }
            reader.close();
            System.out.println("Status table populated successfully.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void sprintResult() {
        String sql = "INSERT INTO sprintResult (resultID, raceID, driverID, constructorID, number, grid, position, positionOrder, points, laps, time, milliseconds, fastestLap, fastestLapTime, statusID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        file = "csv_files/sprint_results.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int resultID = Integer.parseInt(fields[0].trim());
                int raceID = Integer.parseInt(fields[1].trim());
                int driverID = Integer.parseInt(fields[2].trim());
                int constructorID = Integer.parseInt(fields[3].trim());
                int number = Integer.parseInt(fields[4].trim());
                int grid = Integer.parseInt(fields[5].trim());
                Integer position = fields[6].trim().equals("\\N") ? null : Integer.parseInt(fields[6].trim());
                int positionOrder = Integer.parseInt(fields[8].trim());
                BigDecimal points = new BigDecimal(fields[9].trim());
                int laps = Integer.parseInt(fields[10].trim());
                String time = fields[11].trim().equals("\\N") ? null : fields[10].trim();
                Integer milliseconds = fields[12].trim().equals("\\N") ? null : Integer.parseInt(fields[12].trim());
                Integer fastestLap = fields[13].trim().equals("\\N") ? null : Integer.parseInt(fields[13].trim());
                String fastestLapTime = fields[14].trim().equals("\\N") ? null : fields[14].trim();
                int statusID = Integer.parseInt(fields[15].trim());


                preparedStatement.setInt(1, resultID);
                preparedStatement.setInt(2, raceID);
                preparedStatement.setInt(3, driverID);
                preparedStatement.setInt(4, constructorID);
                preparedStatement.setInt(5, number);
                preparedStatement.setInt(6, grid);

                if (position != null) {
                    preparedStatement.setInt(7, position);
                } else {
                    preparedStatement.setNull(7, Types.INTEGER);
                }

                preparedStatement.setInt(8, positionOrder);
                preparedStatement.setBigDecimal(9, points);
                preparedStatement.setInt(10, laps);
                if (time != null) {
                    preparedStatement.setString(11, time);
                } else {
                    preparedStatement.setNull(11, Types.VARCHAR);
                }

                if (milliseconds != null) {
                    preparedStatement.setInt(12, milliseconds);
                } else {
                    preparedStatement.setNull(12, Types.INTEGER);
                }

                if (fastestLap != null) {
                    preparedStatement.setInt(13, fastestLap);
                } else {
                    preparedStatement.setNull(13, Types.INTEGER);
                }

                if (fastestLapTime != null) {
                    preparedStatement.setString(14, fastestLapTime);
                } else {
                    preparedStatement.setNull(14, Types.VARCHAR);
                }

                preparedStatement.setInt(15, statusID);

                preparedStatement.executeUpdate();
            }
            System.out.println("SprintResult table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
        }
    }

    public void driverStanding() {
        String sql = "INSERT INTO driverStanding (driverStandingID, raceID, driverID, points, position, wins) VALUES (?, ?, ?, ?, ?, ?)";
        String file = "csv_files/driver_standings.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                try {
                    int driverStandingID = Integer.parseInt(fields[0].trim());
                    int raceID = Integer.parseInt(fields[1].trim());
                    int driverID = Integer.parseInt(fields[2].trim());
                    BigDecimal points = new BigDecimal(fields[3].trim());
                    int position = Integer.parseInt(fields[4].trim());
                    int wins = Integer.parseInt(fields[6].trim());

                    preparedStatement.setInt(1, driverStandingID);
                    preparedStatement.setInt(2, raceID);
                    preparedStatement.setInt(3, driverID);
                    preparedStatement.setBigDecimal(4, points);
                    preparedStatement.setInt(5, position);
                    preparedStatement.setInt(6, wins);

                    preparedStatement.executeUpdate();
                } catch (NumberFormatException e) {
                    System.out.println("Skipping invalid line: " + line);
                    e.printStackTrace();
                }
            }

            System.out.println("DriverStanding table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("CSV file not found.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
        }
    }

    public void constructorResult() {
        String sql = "INSERT INTO constructorResults (constructorResultsID, raceID,constructorID,points,status) VALUES (?, ?,?,?,?)";
        file = "csv_files/constructor_results.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int constructorResultsID = Integer.parseInt(fields[0].trim());
                int raceID = Integer.parseInt(fields[1].trim());
                int constructorID = Integer.parseInt(fields[2].trim());
                BigDecimal points = new BigDecimal(fields[3].trim());
                String status = fields[4].trim();

                preparedStatement.setInt(1, constructorResultsID);
                preparedStatement.setInt(2, raceID);
                preparedStatement.setInt(3, constructorID);
                preparedStatement.setBigDecimal(4, points);


                if (!status.equals("\\N") && !status.isEmpty()) {
                    preparedStatement.setString(5, status);
                } else {
                    preparedStatement.setNull(5, Types.VARCHAR);
                }

                preparedStatement.executeUpdate();
            }

            System.out.println("ConstructorResults table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    public void constructorStanding() {
        String sql = "INSERT INTO constructorStanding (constructorStandingID, raceID,wins,points,position,constructorID) VALUES (?, ?,?,?,?,?)";
        file = "csv_files/constructor_standings.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int constructorStandingID = Integer.parseInt(fields[0].trim());
                int raceID = Integer.parseInt(fields[1].trim());
                int wins = Integer.parseInt(fields[6].trim());
                BigDecimal points = new BigDecimal(fields[3].trim());
                int position = Integer.parseInt(fields[4].trim());
                int constructorID = Integer.parseInt(fields[2].trim());

                preparedStatement.setInt(1, constructorStandingID);
                preparedStatement.setInt(2, raceID);
                preparedStatement.setInt(3, wins);
                preparedStatement.setBigDecimal(4, points);
                preparedStatement.setInt(5, position);
                preparedStatement.setInt(6, constructorID);

                preparedStatement.executeUpdate();
            }
            System.out.println("ConstructorStanding table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

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
                String formattedDur = parseTime3(columns[5].trim());
                stmt.setString(6, formattedDur.replace("00:", ""));
                stmt.setInt(7, Integer.parseInt(columns[6].trim())); // milliseconds
                stmt.executeUpdate();
                stmt.close();
            }
            reader.close();
            System.out.println("Pitstop table populated successfully.");
        } catch (IOException | SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void qualifyingRecord() {
        String sql = "INSERT INTO qualifyingRecord (qualifyID, raceID, driverID, constructorID, number, position, q1, q2, q3) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        file = "csv_files/qualifying.csv";

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine(); // Skip the header row

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int qualifyID = Integer.parseInt(fields[0].trim());
                int raceID = Integer.parseInt(fields[1].trim());
                int driverID = Integer.parseInt(fields[2].trim());
                int constructorID = Integer.parseInt(fields[3].trim());
                int number = Integer.parseInt(fields[4].trim());
                int position = Integer.parseInt(fields[5].trim());

                // Parse q1, q2, q3 or set them as NULL if `\N`
                Double q1 = fields[6].trim().equals("\\N") ? null : parseTimeToDecimal(fields[6].trim());
                Double q2 = fields[7].trim().equals("\\N") ? null : parseTimeToDecimal(fields[7].trim());
                Double q3 = fields[8].trim().equals("\\N") ? null : parseTimeToDecimal(fields[8].trim());

                preparedStatement.setInt(1, qualifyID);
                preparedStatement.setInt(2, raceID);
                preparedStatement.setInt(3, driverID);
                preparedStatement.setInt(4, constructorID);
                preparedStatement.setInt(5, number);
                preparedStatement.setInt(6, position);

                // Use `setNull` for null values, or `setDouble` for non-null
                if (q1 == null) {
                    preparedStatement.setNull(7, java.sql.Types.DECIMAL);
                } else {
                    preparedStatement.setDouble(7, q1);
                }
                if (q2 == null) {
                    preparedStatement.setNull(8, java.sql.Types.DECIMAL);
                } else {
                    preparedStatement.setDouble(8, q2);
                }
                if (q3 == null) {
                    preparedStatement.setNull(9, java.sql.Types.DECIMAL);
                } else {
                    preparedStatement.setDouble(9, q3);
                }

                preparedStatement.executeUpdate();
            }
            System.out.println("QualifyingRecord table populated successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

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
            System.out.println("Laptime table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    public void drivesFor() {
        String sql = "INSERT INTO drivesFor (constructorID, driverID) VALUES (?, ?)";
        file = "csv_files/drivesFor.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int constructorID = Integer.parseInt(fields[0].trim());
                int driverID = Integer.parseInt(fields[1].trim());


                preparedStatement.setInt(1, constructorID);
                preparedStatement.setInt(2, driverID);

                preparedStatement.executeUpdate();
            }

            System.out.println("DrivesFor table populated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("csv file not found.");
        } catch (IOException e) {
            System.out.println("Error reading csv file.");
        }
    }

    public void compete() {
        String sql = "INSERT INTO compete (raceID, driverID) VALUES (?, ?)";
        file = "csv_files/compete.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int raceID = Integer.parseInt(fields[0].trim());
                int driverID = Integer.parseInt(fields[1].trim());


                preparedStatement.setInt(1, raceID);
                preparedStatement.setInt(2, driverID);

                preparedStatement.executeUpdate();
            }
            System.out.println("Compete table populated successfully.");

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
            double minutes = Double.parseDouble(parts[0]) * 60;
            double seconds = Double.parseDouble(parts[1]);
            return minutes + seconds;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format: " + time, e);
        }
    }


    // Helper method to parse date
    private java.sql.Date parseDate(String date) {
        try {
            date = date.replace("\"", "").trim();
            return date.equals("\\N") || date.isEmpty() ? null : java.sql.Date.valueOf(date);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date format: " + date, e);
        }
    }

    // Helper method to parse time
    private java.sql.Time parseTime(String time) {
        try {
            time = time.replace("\"", "").trim();
            return time.equals("\\N") || time.isEmpty() ? null : java.sql.Time.valueOf(time);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid time format: " + time, e);
        }
    }


    private String parseTime1(String lapTime) {
        lapTime = lapTime.replace("\"", "").trim();

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

        LocalTime parsedTime;

        try {
            if (lapTime.matches("\\d{1,2}:\\d{2}\\.\\d{3}")) {
                String[] parts = lapTime.split("[:\\.]");
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                int millis = Integer.parseInt(parts[2]);

                int hours = minutes / 60;
                minutes = minutes % 60;

                parsedTime = LocalTime.of(hours, minutes, seconds, millis * 1_000_000);
            } else if (lapTime.matches("\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}")) {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:mm:ss.SSS");
                parsedTime = LocalTime.parse(lapTime, inputFormatter);
            } else {
                throw new IllegalArgumentException("Invalid time format: " + lapTime);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time format: " + lapTime, e);
        }

        return parsedTime.format(outputFormatter);
    }

    public static String parseTime2(String timeString) throws ParseException {
        try {
            timeString = timeString.replace("\"", "").trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime time = LocalTime.parse(timeString, formatter);
            return time.toString();
        } catch (Exception e) {
            throw new ParseException("Unable to parse time: " + timeString, 0);
        }
    }

    public static String parseTime3(String durationString) throws ParseException {
        try {
            durationString = durationString.replace("\"", "").trim();

            // Check if the format matches mm:ss.SSS
            if (durationString.contains(":")) {
                // Assume mm:ss.SSS format (e.g., "16:44.718")
                String[] parts = durationString.split(":");
                int minutes = Integer.parseInt(parts[0]);
                String[] secondsAndMillis = parts[1].split("\\.");
                int seconds = Integer.parseInt(secondsAndMillis[0]);
                int millis = Integer.parseInt(secondsAndMillis[1]);

                // Convert minutes and seconds to a LocalTime
                LocalTime time = LocalTime.of(0, minutes, seconds, millis * 1_000_000);
                return time.toString();
            } else {
                // Assume ss.SSS format (e.g., "22.534")
                String[] parts = durationString.split("\\.");
                int seconds = Integer.parseInt(parts[0]);
                int millis = Integer.parseInt(parts[1]);

                // Convert seconds and milliseconds to LocalTime
                LocalTime time = LocalTime.of(0, 0, seconds, millis * 1_000_000);
                return time.toString();
            }
        } catch (Exception e) {
            throw new ParseException("Invalid duration format: " + durationString, 0);
        }
    }


    public void deleteDatabaseData() {
        String sqlFilePath = "formula1_cpg12.sql"; // File containing SQL commands

        try (BufferedReader br = new BufferedReader(new FileReader(sqlFilePath))) {
            StringBuilder sql = new StringBuilder();
            String line;

            // Read the SQL file line by line
            while ((line = br.readLine()) != null) {
                sql.append(line).append("\n");
            }

            try (Connection connection = DriverManager.getConnection(connectionUrl);
                 PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                int rowsAffected = statement.executeUpdate();
                System.out.println("Database data deleted successfully. Rows affected: " + rowsAffected);
            } catch (SQLException e) {
                System.err.println("SQL Error while deleting database data: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.err.println("Error reading SQL file for deleting data: " + e.getMessage());
            e.printStackTrace();
        }
    }

}



