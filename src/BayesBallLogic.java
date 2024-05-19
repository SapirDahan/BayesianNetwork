import java.util.HashMap;

public class BayesBallLogic {

    // Perform Bayes Ball
    public static boolean performBayesBall(String line) {
        // Split the line by the "|" character
        String[] splitLine = line.split("\\|");


        // Get the variables in the query
        String[] vals = splitLine[0].split("-");
        String val1 = vals[0];
        String val2 = vals[1];

        String[] evidences = null;
        if (splitLine.length > 1) {
            // Get the variables in the evidence
            String evidence = splitLine[1];
            evidences = line.split(",");

            // In evidence, remove everything after the equals sign
            for (int i = 0; i < evidences.length; i++) {
                String[] evidenceSplit = evidences[i].split("=");
                evidences[i] = evidenceSplit[0];
            }
        }


        // Determine if the variables are independent
        return areVariablesIndependent(val1, val2, evidences);
    }

    // Determine if the variables are independent
    private static boolean areVariablesIndependent(String val1, String val2, String[] evidences) {
        // Go through v1 children with BFS
        return true;
    }
}
