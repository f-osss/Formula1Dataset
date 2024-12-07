import java.io.*;
import java.util.*;

public class FilterDrivesFor {

    public static void main(String[] args) {
        String inputFile = "csv_files/drivesFor2.csv";
        String outputFile = "csv_files/drivesFor.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {


            Set<String> uniquePairs = new HashSet<>();
            String line;

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");

                String driverID = fields[0].trim();
                String constructorID = fields[1].trim();

                String pair = driverID + "-" + constructorID;


                if (uniquePairs.add(pair)) {
                    bw.write(line);
                    bw.newLine();
                }
            }

            System.out.println("Filtered data has been written to " + outputFile);

        } catch (FileNotFoundException e) {
            System.out.println("The file " + inputFile + " was not found.");
        } catch (IOException e) {
            System.out.println("Error reading or writing to file.");
            e.printStackTrace();
        }
    }
}
