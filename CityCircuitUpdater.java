import java.io.*;
import java.util.*;

public class CityCircuitUpdater {

    public static void main(String[] args) {
        String circuitFile = "csv_files/circuits.csv";
        String cityFile = "csv_files/city.csv";

        // Create a map to store city names and their corresponding cityID from city.csv
        Map<String, Integer> cityMap = new HashMap<>();

        // Read the city.csv file and populate the cityMap
        try (BufferedReader cityReader = new BufferedReader(new FileReader(cityFile))) {
            String line;
            cityReader.readLine();  // Skip header line
            while ((line = cityReader.readLine()) != null) {
                String[] fields = line.split(",");
                String cityName = fields[1].trim();  // Assuming city name is in the second column
                int cityID = Integer.parseInt(fields[0].trim()); // Assuming cityID is in the first column
                cityMap.put(cityName, cityID);
            }
        } catch (IOException e) {
            System.out.println("Error reading city file: " + e.getMessage());
            return;
        }

        // List to hold the updated data for circuits.csv
        List<String> updatedCircuits = new ArrayList<>();

        // Read the circuits.csv file, update cityID, and collect the updated data
        try (BufferedReader circuitReader = new BufferedReader(new FileReader(circuitFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = circuitReader.readLine()) != null) {
                if (isFirstLine) {
                    updatedCircuits.add(line);  // Add the header line
                    isFirstLine = false;
                    continue;
                }

                String[] fields = line.split(",");
                String location = fields[4].trim();  // Assuming location is in the fifth column (index 4)

                // Get the cityID for the location, default to -1 if not found
                Integer cityID = cityMap.get(location);

                if (cityID != null) {
                    // Replace the cityID in the circuit data (update the cityID column)
                    fields[1] = String.valueOf(cityID); // Assuming cityID is in the second column (index 1)
                } else {
                    System.out.println("City not found for location: " + location);
                }

                // Join the updated fields and add to the list
                updatedCircuits.add(String.join(",", fields));
            }

            // Write the updated content back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(circuitFile))) {
                for (String updatedLine : updatedCircuits) {
                    writer.write(updatedLine);
                    writer.newLine();
                }
            }

            System.out.println("circuits.csv successfully updated!");
        } catch (IOException e) {
            System.out.println("Error reading or writing circuit file: " + e.getMessage());
        }
    }
}
