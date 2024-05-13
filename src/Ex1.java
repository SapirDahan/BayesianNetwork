import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Ex1 {
    public static void main(String[] args) {

        // Create a Bayesian network
        BayesianNetworkManager network = BayesianNetworkManager.getInstance();

        String fileName = "src/input.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String XMLFileName = reader.readLine(); // Read the first line


            // Read the XML file and create the Bayesian network
            XMLDataReader.readXMLFile(XMLFileName);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }




//        // Iterate over the other lines of input
//        String line;
//
//        while ((line = System.console().readLine()) != null) {
//            // Check if to perform variable elimination or Bayes Ball
//
//            // If the line start with "P(" then perform Bayes Ball
//            if (line.startsWith("P(")) {
//                // Perform Bayes Ball
//                BayesBall.performBayesBall(line);
//            }
//
//            else {
//                // Perform variable elimination
//                VariableEliminationLogic.performVariableElimination(line);
//            }
//        }


    }
}