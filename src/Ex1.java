import java.io.*;

public class Ex1 {
    public static void main(String[] args) {

        // Create a Bayesian network
        BayesianNetworkManager network = BayesianNetworkManager.getInstance();

        String fileName = "src/input.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String XMLFileName = reader.readLine(); // Read the first line

            // Read the XML file and create the Bayesian network
            XMLDataReader.readXMLFile(XMLFileName);

            // Create file output.txt if it does not exist and clear it
            File file = new File("src/output.txt");

            // Clear the file
            FileWriter fileClear = new FileWriter(file);
            fileClear.write("");
            fileClear.close();


            // Read the queries and perform the required operation
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if to perform variable elimination or Bayes Ball

                // If the line start with "P(" then perform variable elimination
                if (line.startsWith("P(")) {
                    // Perform variable elimination
                    String result = VariableEliminationLogic.performVariableElimination(line);
                    FileWriter fileWriter = new FileWriter(file, true);

                    // Write the result to the output file and add a new line if there is another query
                    if(reader.ready()){
                        fileWriter.write(result + "\n");
                    }
                    else{
                        fileWriter.write(result);
                    }
                    fileWriter.close();

                }

                else {
                    // Perform Bayes Ball
                    Boolean independent = BayesBallLogic.performBayesBall(line);

                    // Write the result to the output file
                    FileWriter fileWriter = new FileWriter(file, true);
                    if (independent) {
                        // Write "yes" to the output file
                        fileWriter.write("yes");
                    }
                    else {
                        // Write "no" to the output file
                        fileWriter.write("no");
                    }

                    // Write a new line if there is another query
                    if(reader.ready()){
                        fileWriter.write("\n");
                    }
                    fileWriter.close();

                }
            }
        }
        catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}