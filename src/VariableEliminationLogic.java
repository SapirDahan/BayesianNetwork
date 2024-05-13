//
//public class VariableEliminationLogic {
//
//    // Perform variable elimination
//
//    // To go over @@@
//    public static void performVariableElimination(String line) {
//        // Split the line by the "|" character
//        String[] splitLine = line.split("\\|");
//
//        // Get the variables in the query
//        String query = splitLine[0];
//
//        // Get the variables in the evidence
//        String evidence = splitLine[1];
//
//        // Split the query by the "," character
//        String[] queryVariables = query.split(",");
//
//        // Split the evidence by the "," character
//        String[] evidenceVariables = evidence.split(",");
//
//        // Get the variables in the network
//        BayesianNetworkManager network = BayesianNetworkManager.getInstance();
//
//        // Get the variables in the network
//        HashMap<String, Variable> variables = network.getVariables();
//
//        // Get the variables in the network
//        HashMap<String, Variable> hiddenVariables = new HashMap<String, Variable>();
//
//        // Iterate over the variables in the network
//        for (String variableName : variables.keySet()) {
//            // Check if the variable is not in the query or evidence
//            if (!Arrays.asList(queryVariables).contains(variableName) && !Arrays.asList(evidenceVariables).contains(variableName)) {
//                // Add the variable to the hidden variables
//                hiddenVariables.put(variableName, variables.get(variableName));
//            }
//        }
//
//        // Get the hidden variables
//        Variable[] hiddenVariablesArray = hiddenVariables.values().toArray(new Variable[hiddenVariables.size()]);
//
//        // Get the query variables
//        Variable[] queryVariablesArray = new Variable[queryVariables.length];
//
//        // Get the evidence variables
//        Variable[] evidenceVariablesArray = new Variable[evidenceVariables.length];
//
//        // Iterate over the query variables
//        for (int i = 0; i < queryVariables.length; i++) {
//            // Get the variable
//            queryVariablesArray[i] = variables.get(queryVariables[i]);
//        }
//
//        // Iterate over the evidence variables
//        for (int i = 0; i < evidenceVariables.length; i++) {
//            // Get the variable
//            evidenceVariablesArray[i] = variables.get(evidenceVariables[i]);
//        }
//
//        // Perform variable elimination
//        double[] result = variableElimination(queryVariablesArray, evidenceVariablesArray, hiddenVariablesArray);
//
//        // Print the result
//        for (int i = 0; i < result.length; i++) {
//            System.out.print(result[i] + " ");
//        }
//    }
//}
