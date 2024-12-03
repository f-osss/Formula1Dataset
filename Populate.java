import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Statement;

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
//        race();
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
}
