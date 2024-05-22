import java.util.HashMap;

public class VariableEliminationLogic {

    // Perform variable elimination
    // Count the number of addition and multiplication operations
    private static int additionOperations = 0;
    private static int multiplicationOperations = 0;

    public static String performVariableElimination(String line) {

        // The final result
        double result = 0.0;

        // Reset the number of addition and multiplication operations
        additionOperations = 0;
        multiplicationOperations = 0;

        /// P(B=T|J=T,M=T) A-E

        // Split the line by the " " character
        String[] splitLine = line.split(" ");

        // From the first element, remove the "P(" characters and the last character ")"
        String query = splitLine[0].substring(2, splitLine[0].length() - 1);

        // Split the query by the "|" character
        String[] querySplit = query.split("\\|");

        // Get the variables in the query
        String[] valParts = querySplit[0].split("=");
        String[] variableValuePair = new String[2];

        variableValuePair[0] = valParts[0]; // Left value
        variableValuePair[1] = valParts[1]; // Right value


//        // Save in 2D array the values of the variables and the variables, split by "="
//        String[][] variableValuePairs = new String[vals.length][2];
//        for (int i = 0; i < vals.length; i++) {
//            String[] parts = vals[i].split("=");
//            variableValuePairs[i][0] = parts[0]; // Left value
//            variableValuePairs[i][1] = parts[1]; // Right value
//        }

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
        String[] hidden;
        if(splitLine.length > 1){
            hidden = splitLine[1].split("-");
        }
        else{
            hidden = null;
        }

//        System.out.println("------------------------");
//        // Print the variableValuePair
//
//        System.out.println("vari: " + variableValuePair[0] + " == " + variableValuePair[1]);
//
//
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


        PCT OriginalFactor = new PCT(BayesianNetworkManager.getInstance().getVariable(variableValuePair[0]));
        double resultExistInCPT = OriginalFactor.getValue(variableValuePair, evidenceValuePairs);
        if (resultExistInCPT != -1) {
            return String.format("%.5f", resultExistInCPT) + "," + additionOperations + "," + multiplicationOperations;
        }


        // Return the result, formatted to 5 decimal places, and the number of addition and multiplication operations
        return String.format("%.5f", result) + "," + additionOperations + "," + multiplicationOperations;
    }

}
