import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

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

        constructor();
        constructorResult();
//        race();
//        sprintResult();
//        //----------------------------------------------TEST-----------------------------------------------
//        ResultSet resultSet = null;
//        try (Connection connection = DriverManager.getConnection(connectionUrl);
//             Statement statement = connection.createStatement();) {
//
//            // Create and execute a SELECT SQL statement.
//            String selectSql = "SELECT * FROM constructor;";
//            resultSet = statement.executeQuery(selectSql);
//
//            // Print results from select statement
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString(1) +
//                        resultSet.getString(2) +
//                         resultSet.getString(3));
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        //----------------------------------------------TEST-----------------------------------------------


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


    public void constructorResult()
    {
        String sql = "INSERT INTO constructorResults (raceID,constructorID,points,status) VALUES (?, ?,?,?)";
        file = "csv_files/constructors_results.csv";
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             BufferedReader br = new BufferedReader(new FileReader(file));
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                int raceID =  Integer.parseInt(fields[1].trim());
                int constructorID =  Integer.parseInt(fields[2].trim());
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




    public void circuit() {
        String sql = "INSERT INTO circuit (cityID, circuitRef, name, long, lat, altitude) VALUES (?, ?, ?, ?, ?, ?)";
        file = "csv_files/circuit.csv";
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
                preparedStatement.setString(5, date);

                if (!time.equals("\\N") && !time.isEmpty()) {
                    preparedStatement.setString(6, time);
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

    public void sprintResult() {
        String sql = "INSERT INTO sprintResult (raceID, driverID, constructorID, number, grid, position, positionOrder, points, laps, time, milliseconds, fastestLap, fastestLapTime, statusID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";        file = "csv_files/sprint_results.csv";
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

//    // Chuka table inserts starts here
//    // Insert data into 'driver' table from CSV file
//    private void insertDriverData() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("driver.csv"));
//            reader.readLine(); // Skip header
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] columns = line.split(",");
//                PreparedStatement stmt = connection.prepareStatement("INSERT INTO driver (driverID, forename, surname, dob, nationality) VALUES (?, ?, ?, ?, ?)");
//                stmt.setInt(1, Integer.parseInt(columns[0]));
//                stmt.setString(2, columns[1]);
//                stmt.setString(3, columns[2]);
//                stmt.setDate(4, Date.valueOf(columns[3]));
//                stmt.setString(5, columns[4]);
//                stmt.executeUpdate();
//                stmt.close();
//            }
//            reader.close();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Insert data into 'result' table from CSV file
//    private void insertResultData() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("result.csv"));
//            reader.readLine(); // Skip header
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] columns = line.split(",");
//                PreparedStatement stmt = connection.prepareStatement("INSERT INTO result (raceID, driverID, positionOrder, points, timeGap) VALUES (?, ?, ?, ?, ?)");
//                stmt.setInt(1, Integer.parseInt(columns[0]));
//                stmt.setInt(2, Integer.parseInt(columns[1]));
//                stmt.setInt(3, Integer.parseInt(columns[2]));
//                stmt.setInt(4, Integer.parseInt(columns[3]));
//                stmt.setDouble(5, Double.parseDouble(columns[4]));
//                stmt.executeUpdate();
//                stmt.close();
//            }
//            reader.close();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Insert data into 'pitstop' table from CSV file
//    private void insertPitstopData() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("pitstop.csv"));
//            reader.readLine(); // Skip header
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] columns = line.split(",");
//                PreparedStatement stmt = connection.prepareStatement("INSERT INTO pitstop (raceID, driverID, stopTime, pitStopDuration) VALUES (?, ?, ?, ?)");
//                stmt.setInt(1, Integer.parseInt(columns[0]));
//                stmt.setInt(2, Integer.parseInt(columns[1]));
//                stmt.setTimestamp(3, Timestamp.valueOf(columns[2]));
//                stmt.setDouble(4, Double.parseDouble(columns[3]));
//                stmt.executeUpdate();
//                stmt.close();
//            }
//            reader.close();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Insert data into 'status' table from CSV file
//    private void insertStatusData() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("status.csv"));
//            reader.readLine(); // Skip header
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] columns = line.split(",");
//                PreparedStatement stmt = connection.prepareStatement("INSERT INTO status (statusID, description) VALUES (?, ?)");
//                stmt.setInt(1, Integer.parseInt(columns[0]));
//                stmt.setString(2, columns[1]);
//                stmt.executeUpdate();
//                stmt.close();
//            }
//            reader.close();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Insert data into 'laptime' table from CSV file
//    private void insertLapTimeData() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("laptime.csv"));
//            reader.readLine(); // Skip header
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] columns = line.split(",");
//                PreparedStatement stmt = connection.prepareStatement("INSERT INTO laptime (raceID, driverID, lapTime) VALUES (?, ?, ?)");
//                stmt.setInt(1, Integer.parseInt(columns[0]));
//                stmt.setInt(2, Integer.parseInt(columns[1]));
//                stmt.setDouble(3, Double.parseDouble(columns[2]));
//                stmt.executeUpdate();
//                stmt.close();
//            }
//            reader.close();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Insert data into 'records' table from CSV file
//    private void insertRecordsData() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader("records.csv"));
//            reader.readLine(); // Skip header
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] columns = line.split(",");
//                PreparedStatement stmt = connection.prepareStatement("INSERT INTO records (raceID, driverID, recordType, value) VALUES (?, ?, ?, ?)");
//                stmt.setInt(1, Integer.parseInt(columns[0]));
//                stmt.setInt(2, Integer.parseInt(columns[1]));
//                stmt.setString(3, columns[2]);
//                stmt.setString(4, columns[3]);
//                stmt.executeUpdate();
//                stmt.close();
//            }
//            reader.close();
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        }
//    }
//    // Chuka table inserts end here

    

}
