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

    // @@@ go over logic
    public static boolean areVariablesIndependent(String val1, String val2, String[] evidences) {

        // If the variables are the same, they are dependent
        if (val1.equals(val2)) {
            return false;
        }

        // If the variables are connected by evidence, they are dependent, if the evidence is not empty
        if (evidences != null) {
            for (String evidence : evidences) {
                if (val1.equals(evidence) || val2.equals(evidence)) {
                    return false;
                }
            }
        }


        // If the variables are connected by a common parent, they are dependent
        if (areVariablesConnected(val1, val2)) {
            return false;
        }

        // If the variables are connected by a common child, they are dependent
        if (areVariablesConnected(val2, val1)) {
            return false;
        }

        // If the variables are not connected, they are independent
        return true;
    }

    // Determine if the variables are connected
    public static boolean areVariablesConnected(String val1, String val2) {
        // Get the variables in the network
        BayesianNetworkManager network = BayesianNetworkManager.getInstance();
        HashMap<String, Variable> variables = network.getVariables();

        // Get the variable from the network
        Variable variable = BayesianNetworkManager.getInstance().getVariable(val1);
        //Variable variable = variables.get(val1);

        // Get the parents of the variable
        Variable[] parents = variable.getParents();


        if (parents != null) {
            // Iterate over the parents
            for (int i = 0; i < parents.length; i++) {
                // If the parent is the second variable, they are connected
                if (parents[i].getName().equals(val2)) {
                    return true;
                }
            }
        }


        // If the variables are not connected, they are not connected
        return false;
    }
}
