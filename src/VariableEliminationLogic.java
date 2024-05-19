import java.util.HashMap;

public class VariableEliminationLogic {

    // Perform variable elimination

    // @@@ val is always one or can be more? chane the array from 2D to 1 D
    public static String performVariableElimination(String line) {
        // Count the number of addition and multiplication operations
        int additionOperations = 0;
        int multiplicationOperations = 0;

        // The final result
        double result = 0.0;

        /// P(B=T|J=T,M=T) A-E

        // Split the line by the " " character
        String[] splitLine = line.split(" ");

        // From the first element, remove the "P(" characters and the last character ")"
        String query = splitLine[0].substring(2, splitLine[0].length() - 1);

        // Split the query by the "|" character
        String[] querySplit = query.split("\\|");

        // Get the variables in the query
        String[] vals = querySplit[0].split(",");

        // Save in 2D array the values of the variables and the variables, split by "="
        String[][] variableValuePairs = new String[vals.length][2];
        for (int i = 0; i < vals.length; i++) {
            String[] parts = vals[i].split("=");
            variableValuePairs[i][0] = parts[0]; // Left value
            variableValuePairs[i][1] = parts[1]; // Right value
        }

        // Get the evidence in the query
        String[] evidence = querySplit[1].split(",");

        // Save in 2D array the values of the evidence and the evidence, split by "="
        String[][] evidenceValuePairs = new String[evidence.length][2];
        for (int i = 0; i < evidence.length; i++) {
            String[] parts = evidence[i].split("=");
            evidenceValuePairs[i][0] = parts[0]; // Left value
            evidenceValuePairs[i][1] = parts[1]; // Right value
        }

        // Get the hidden variables
        String[] hidden = splitLine[1].split("-");

//        System.out.println("------------------------");
//        // print all the variables
//        for (String val : vals) {
//            System.out.println("vals: " + val);
//        }
//
//        // print all the variables split
//        for (String[] valSplit : variableValuePairs) {
//            System.out.println("valSplit: " + valSplit[0] + " " + valSplit[1]);
//        }
//
//        // print all the evidence
//        for (String ev : evidence) {
//            System.out.println("ev: " + ev);
//        }
//
//        // print all the evidence split
//        for (String[] evSplit : evidenceValuePairs) {
//            System.out.println("evSplit: " + evSplit[0] + " " + evSplit[1]);
//        }
//
//        // print all the hidden
//        for (String hid : hidden) {
//            System.out.println("hid: " + hid);
//        }

        // Get the variables from the network
        HashMap<String, Variable> variables = BayesianNetworkManager.getInstance().getVariables();


        //Factor resultFactor = new Factor(BayesianNetworkManager.getInstance().getVariable("A"));

        // Check if we can have the value from the factor table
        Factor OriginalFactor = new Factor(BayesianNetworkManager.getInstance().getVariable(vals[0]));

        // Try to get the value from the factor table
        double result = OriginalFactor.getValue(vals, evidenceValuePairs);



        // Return the result, formatted to 5 decimal places, and the number of addition and multiplication operations
        return String.format("%.5f", result) + "," + additionOperations + "," + multiplicationOperations;
    }

}
