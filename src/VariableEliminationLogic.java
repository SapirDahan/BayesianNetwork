import java.util.*;

public class VariableEliminationLogic {

    // Perform variable elimination
    // Count the number of addition and multiplication operations
    private static int additionOperations = 0;
    private static int multiplicationOperations = 0;

    public static String performVariableElimination(String line) {

        // The final result
        double result = 0.0;

        // Reset the number of addition and multiplication operations
        resetOperationCounts();

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
            return formatResult(resultExistInCPT);
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

        // crate double array fill with 0 length 8
        double[] p = new double[8];
        PCT factor1 = new PCT(v, p);

        PCT a = eliminateVariable(factor1, "A");

        // Print the probability table of the new factor
        System.out.println("Factor1: ");
        for (int i = 0; i < a.getPCTTable()[0].length; i++) {
            for (int j = 0; j < a.getPCTTable().length; j++) {
                System.out.print(a.getPCTTable()[j][i] + " ");
            }
            System.out.println();
        }

        // Print the probability table of the new factor
        System.out.println("probability: ");
        for (int i = 0; i < a.getPCTProbability().length; i++) {
            System.out.print(a.getPCTProbability()[i] + " ");
        }

        // Check that the mergePCT function works
//        System.out.println("------------------------");
//
//        PCT factor1 = new PCT(BayesianNetworkManager.getInstance().getVariable("M"));
//
//
//        PCT factor2 = new PCT(BayesianNetworkManager.getInstance().getVariable("J"));
//
//        PCT merged = mergePCT(factor1, factor2, "A");
//
//        // Print the probability table of the new factor
//        System.out.println("Factor1: ");
//        for (int i = 0; i < merged.getPCTTable()[0].length; i++) {
//            for (int j = 0; j < merged.getPCTTable().length; j++) {
//                System.out.print(merged.getPCTTable()[j][i] + " ");
//            }
//            System.out.println();
//        }
//
//        // Print the probability table of the new factor
//        System.out.println("probability: ");
//        for (int i = 0; i < merged.getPCTProbability().length; i++) {
//            System.out.print(merged.getPCTProbability()[i] + " ");
//        }





        // Return the result, formatted to 5 decimal places, and the number of addition and multiplication operations
        return formatResult(result);
    }

    private static void resetOperationCounts() {
        additionOperations = 0;
        multiplicationOperations = 0;
    }

    private static String formatResult(double result) {
        return String.format("%.5f", result) + "," + additionOperations + "," + multiplicationOperations;
    }

    // Marge two PCT
    private static PCT mergePCT(PCT factor1, PCT factor2, String variable) {
        if (factor1 == null) return factor2;
        if (factor2 == null) return factor1;

        if (!factor1.isValidPCT() || !factor2.isValidPCT()) return null;

        Variable[] variablesOrder1 = factor1.getVariablesInOrder();
        Variable[] variablesOrder2 = factor2.getVariablesInOrder();
        Set<String> commonVariables = new HashSet<>();

        for (Variable v1 : variablesOrder1) {
            for (Variable v2 : variablesOrder2) {
                if (v1.getName().equals(v2.getName())) {
                    commonVariables.add(v1.getName());
                }
            }
        }

        List<Variable> newVariablesOrder = new ArrayList<>(Arrays.asList(variablesOrder1));
        for (Variable v2 : variablesOrder2) {
            if (!commonVariables.contains(v2.getName())) {
                newVariablesOrder.add(v2);
            }
        }

        int newPCTLength = 1;
        for (Variable var : newVariablesOrder) {
            newPCTLength *= var.getPossibleValues().length;
        }

        System.out.println("newPCTLength: " + newPCTLength);

        double[] newPCTProbability = new double[newPCTLength];

        String[][] newPCTTable = new String[newVariablesOrder.size()][newPCTLength];
        populateNewPCTTable(newPCTTable, newVariablesOrder);

        for (int i = 0; i < newPCTLength; i++) {
            int index1 = getIndex(factor1, newPCTTable, i, variablesOrder1);
            int index2 = getIndex(factor2, newPCTTable, i, variablesOrder2);
            newPCTProbability[i] = factor1.getPCTProbability()[index1] * factor2.getPCTProbability()[index2];
            multiplicationOperations++;
        }

        PCT newPCT = new PCT(newVariablesOrder.toArray(new Variable[0]), newPCTProbability);
        return newPCT;
    }

    private static int getIndex(PCT factor, String[][] newPCTTable, int rowIndex, Variable[] variablesOrder) {
        int index = 0;
        int multiplier = 1;
        for (int j = variablesOrder.length - 1; j >= 0; j--) {
            String variableValue = newPCTTable[j][rowIndex];
            String[] possibleValues = variablesOrder[j].getPossibleValues();
            int valueIndex = Arrays.asList(possibleValues).indexOf(variableValue);
            index += valueIndex * multiplier;
            multiplier *= possibleValues.length;
        }
        return index;
    }

    private static void populateNewPCTTable(String[][] newPCTTable, List<Variable> newVariablesOrder) {
        int length = newPCTTable[0].length;
        for (int i = 0; i < newVariablesOrder.size(); i++) {
            String[] possibleValues = newVariablesOrder.get(i).getPossibleValues();
            int numValues = possibleValues.length;
            int cycleLength = length / numValues;
            for (int j = 0; j < length; j++) {
                newPCTTable[i][j] = possibleValues[(j / cycleLength) % numValues];
            }
        }
    }


    // Eliminate a variable from a PCT
    private static PCT eliminateVariable(PCT factor, String variable) {
        if (factor == null || !factor.isValidPCT() || !factor.containsVariable(variable)) {
            return null;
        }

        // Get the current variables in the factor
        Variable[] currentVariables = factor.getVariablesInOrder();

        // Create a list of variables excluding the one to be eliminated
        List<Variable> newVariablesOrderList = new ArrayList<>();
        for (Variable var : currentVariables) {
            if (!var.getName().equals(variable)) {
                newVariablesOrderList.add(var);
            }
        }

        // Convert the list back to an array
        Variable[] newVariablesOrder = newVariablesOrderList.toArray(new Variable[0]);

        // Calculate the length of the new PCT
        int newPCTLength = 1;
        for (Variable var : newVariablesOrder) {
            newPCTLength *= var.getPossibleValues().length;
        }

        double[] newPCTProbability = new double[newPCTLength];
        String[][] newPCTTable = new String[newVariablesOrder.length][newPCTLength];
        populateNewPCTTable(newPCTTable, newVariablesOrder);

        // Sum out the variable to be eliminated
        for (int i = 0; i < newPCTLength; i++) {
            for (int j = 0; j < factor.getPCTLength(); j++) {
                boolean match = true;
                for (int k = 0; k < newVariablesOrder.length; k++) {
                    if (!newPCTTable[k][i].equals(factor.getPCTTable()[k][j])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    newPCTProbability[i] += factor.getPCTProbability()[j];
                    additionOperations++;
                }
            }
        }

        return new PCT(newVariablesOrder, newPCTProbability);
    }

    private static void populateNewPCTTable(String[][] newPCTTable, Variable[] newVariablesOrder) {
        int length = newPCTTable[0].length;
        for (int i = 0; i < newVariablesOrder.length; i++) {
            String[] possibleValues = newVariablesOrder[i].getPossibleValues();
            int numValues = possibleValues.length;
            int cycleLength = length / numValues;
            for (int j = 0; j < length; j++) {
                newPCTTable[i][j] = possibleValues[(j / cycleLength) % numValues];
            }
        }
    }

}
