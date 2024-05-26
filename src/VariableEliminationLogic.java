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

//        // Check the second constructor in PCT
//        Variable[] v = {BayesianNetworkManager.getInstance().getVariable("A"), BayesianNetworkManager.getInstance().getVariable("B"), BayesianNetworkManager.getInstance().getVariable("E")};
//
//        // Creade double array fill with 0 length 8
//        double[] p = new double[8];
//        PCT factor1 = new PCT(v, p);
//
//        if(factor1.isValidPCT()){
//            // Print the factor1
//            System.out.println("Factor1: ");
//            for (int i = 0; i < factor1.getPCTTable()[0].length; i++) {
//                for (int j = 0; j < factor1.getPCTTable().length; j++) {
//                    System.out.print(factor1.getPCTTable()[j][i] + " ");
//                }
//                System.out.println();
//            }
//        }

        // Check that the eliminateVariable function works
        Variable[] v = {BayesianNetworkManager.getInstance().getVariable("A"), BayesianNetworkManager.getInstance().getVariable("B"), BayesianNetworkManager.getInstance().getVariable("E")};

//        // crate double array fill with 0 length 8
//        double[] p = new double[8];
//        PCT factor1 = new PCT(v, p);
//
//        PCT a = eliminateVariable(factor1, "A");
//
//        // Print the probability table of the new factor
//        System.out.println("Factor1: ");
//        for (int i = 0; i < a.getPCTTable()[0].length; i++) {
//            for (int j = 0; j < a.getPCTTable().length; j++) {
//                System.out.print(a.getPCTTable()[j][i] + " ");
//            }
//            System.out.println();
//        }
//
//        // Print the probability table of the new factor
//        System.out.println("probability: ");
//        for (int i = 0; i < a.getPCTProbability().length; i++) {
//            System.out.print(a.getPCTProbability()[i] + " ");
//        }





        // Return the result, formatted to 5 decimal places, and the number of addition and multiplication operations
        return String.format("%.5f", result) + "," + additionOperations + "," + multiplicationOperations;
    }

    // Marge two PCT @@@ not finished
    private static PCT mergePCT(PCT factor1, PCT factor2, String variable) {

        // If one of the factors is empty, return the other factor
        if(factor1 == null){
            return factor2;
        }
        if(factor2 == null){
            return factor1;
        }

        // If the factors are not valid, return null
        if(!factor1.isValidPCT() || !factor2.isValidPCT()){
            return null;
        }

        // If the variable is not in the factors, return null
        if(!factor1.containsVariable(variable) || !factor2.containsVariable(variable)){
            return null;
        }

        // If one factor is in the other factor, multiply the factors



        return null;

    }

    // Eliminate a variable from a PCT
    private static PCT eliminateVariable(PCT factor, String variable){

        // If the factor is empty, return null
        if(factor == null){
            return null;
        }

        // If the factor is not valid, return null
        if(!factor.isValidPCT()){
            return null;
        }

        // If the variable is not in the factor, return null
        if(!factor.containsVariable(variable)){
            return null;
        }

        // If the variable is the only variable in the factor, return the factor
        if(factor.getVariablesOrder().length == 1){
            return factor;
        }

        // Create a new factor without the variable
        // The variables order in the new factor
        Variable[] newVariablesOrder = new Variable[factor.getVariablesOrder().length - 1];
        int counter = 0;
        for(int i = 0; i < factor.getVariablesOrder().length; i++){
            if(!factor.getVariablesOrder()[i].equals(variable)){
                newVariablesOrder[counter] = factor.getVariablesInOrder()[i];
                counter++;
            }
        }

        // The new factor table
        // Find the length of the new factor table
        int newFactorLength = 1;
        for(int i = 0; i < newVariablesOrder.length; i++){
            newFactorLength *= newVariablesOrder[i].getPossibleValues().length;
        }

        // Create the new PCT
        PCT newFactor = new PCT(newVariablesOrder, new double[newFactorLength]);

        // Create an array to store if a line is already added to the new factor
        boolean[] added = new boolean[factor.getPCTTable()[0].length];

        // Find all the 2 identical line (without the variable) in the factor table and sum them to the new factor
        for(int i = 0; i < factor.getPCTTable()[0].length; i++){

        }



        return newFactor;
    }

}
