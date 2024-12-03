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
            System.out.println("CSV file not found.");
        } catch (IOException e) {
            System.out.println("Error reading CSV file.");
        }
    }
}
