import java.util.*;

public class VariableEliminationLogic {

    // Perform variable elimination
    // Count the number of addition and multiplication operations
    public static int additionOperations = 0;
    public static int multiplicationOperations = 0;

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


        // Get the variables from the network
        HashMap<String, Variable> variables = BayesianNetworkManager.getInstance().getVariables();


        // Create list of variables
        ArrayList<Variable> variablesList = new ArrayList<Variable>();



        // Add only the significant variables to the variablesList
        addSignificantVariables(variablesList, variableValuePair[0], evidenceValuePairs);

        // Create a list of PCTs for the variables int the network
        ArrayList<PCT> PCTs = new ArrayList<PCT>();

        // Create the PCTs for the variables in the network
        for (Variable variable : variablesList) {
            PCT pct = new PCT(variable);
            PCTs.add(pct);
        }


        // interate over all PCTs then iterate over all evidence values and craetes for each PCT a new PCT where the evidence is met then add the new PCT to the PCTs list and remove the old one
        for (int i = 0; i < PCTs.size(); i++) {
            PCT pct = PCTs.get(i);
            for (String[] evidenceValuePair : evidenceValuePairs) {
                if (pct.containsVariable(evidenceValuePair[0])) {
                    pct = new PCT(pct, BayesianNetworkManager.getInstance().getVariable(evidenceValuePair[0]), evidenceValuePair[1]);
                }
            }
            PCTs.set(i, pct);
        }


        // Iterate over all hidden variables
        for (String hiddenVariable : hidden) {

            // Get all the PCTs that contain the hidden variable
            ArrayList<PCT> PCTsWithVariable = getPCTsContainingVariable(PCTs, hiddenVariable);

            // Sort the PCTs by the length ascending order and secondary sort by the ascii value ascending order of the variables
            PCTsWithVariable = sortPCTs(PCTsWithVariable);

            // Print the PCTs table of PCTsWithVariable
            for (PCT pct : PCTsWithVariable) {
                System.out.println("PCTs with " + hiddenVariable);
                printPCTTable(pct.getPCTTable(), pct.getPCTProbability());
                System.out.println("-----------------");
            }

            // Remove the PCTs that contain the hidden variable from the PCTs list
            PCTs.removeAll(PCTsWithVariable);



            if(PCTsWithVariable.size() > 0){
                PCT resultPCT = PCTsWithVariable.get(0);
                for (int i = 1; i < PCTsWithVariable.size(); i++) {
                    resultPCT = new PCT(resultPCT, PCTsWithVariable.get(i));

                    // Print the PCT table result
                    printPCTTable(resultPCT.getPCTTable(), resultPCT.getPCTProbability());
                    System.out.println("-----------------");
                }

                System.out.println("now eliminate " + hiddenVariable);
                resultPCT = new PCT(resultPCT, variables.get(hiddenVariable));

                // Append the resulting PCT to PCTs
                PCTs.add(resultPCT);

                // print PCT table
                printPCTTable(resultPCT.getPCTTable(), resultPCT.getPCTProbability());
                System.out.println("-----------------");

            }

        }


        // Get all the PCTs that contain the hidden variable
        ArrayList<PCT> PCTsWithVariable = getPCTsContainingVariable(PCTs, variableValuePair[0]);

        // Sort the PCTs by the length ascending order and secondary sort by the ascii value ascending order of the variables
        PCTsWithVariable = sortPCTs(PCTsWithVariable);

        PCT resultPCT = PCTsWithVariable.get(0);
        for (int i = 1; i < PCTsWithVariable.size(); i++) {
            resultPCT = new PCT(resultPCT, PCTsWithVariable.get(i));

            // Print the PCT table result
            printPCTTable(resultPCT.getPCTTable(), resultPCT.getPCTProbability());
            System.out.println("-----------------");
        }

        // Normalize the probability table
        double[] probabilityTable = normalizeProbabilityTable(resultPCT.getPCTProbability());

        // Set the probability table of the resultPCT
        resultPCT.setPCTProbability(probabilityTable);

        // Print the PCT table result
        printPCTTable(resultPCT.getPCTTable(), resultPCT.getPCTProbability());

        // Calculate the result
        for (int i = 0; i < probabilityTable.length; i++) {
            if (resultPCT.getPCTTable()[i][0].equals(variableValuePair[1])) {
                result = probabilityTable[i];
                break;
            }
        }



        System.out.println("********************************************************");


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

    // Print PCT table
    private static void printPCTTable(String[][] PCTTable, double[] PCTProbability) {
        for (int j = 0; j < PCTTable[0].length; j++) {
            for (int i = 0; i < PCTTable.length; i++) {
                System.out.print(PCTTable[i][j] + " ");
            }
            // Print the value of the probability
            System.out.print(PCTProbability[j]);
            System.out.println();
        }
    }

    // Get ascii value of a PCTs variables
    private static int getAsciiValue(String[] variables) {
        int asciiValue = 0;
        for (String variable : variables) {
            for (int i = 0; i < variable.length(); i++) {
                asciiValue += (int) variable.charAt(i);
            }
        }
        return asciiValue;
    }

    // Sort the PCTs by the length ascending order and secondary sort by the ascii value ascending order of the variables
    private static ArrayList<PCT> sortPCTs(ArrayList<PCT> PCTs) {
        Collections.sort(PCTs, new Comparator<PCT>() {
            @Override
            public int compare(PCT pct1, PCT pct2) {
                if (pct1.getPCTLength() == pct2.getPCTLength()) {
                    return getAsciiValue(pct1.getVariablesOrder()) - getAsciiValue(pct2.getVariablesOrder());
                }
                return pct1.getPCTLength() - pct2.getPCTLength();
            }
        });
        return PCTs;
    }


    // Get arrayList of PCTs and return all the PCTs that contain the variable
    private static ArrayList<PCT> getPCTsContainingVariable(ArrayList<PCT> PCTs, String variable) {
        ArrayList<PCT> PCTsWithVariable = new ArrayList<PCT>();
        for (PCT pct : PCTs) {
            if (pct.containsVariable(variable)) {
                PCTsWithVariable.add(pct);
            }
        }
        return PCTsWithVariable;
    }

    // Normalize the probability table
    private static double[] normalizeProbabilityTable(double[] probabilityTable) {
        double sum = 0.0;
        for (double probability : probabilityTable) {
            sum += probability;
            additionOperations++;
        }
        additionOperations--;
        for (int i = 0; i < probabilityTable.length; i++) {
            probabilityTable[i] = probabilityTable[i] / sum;
        }
        return probabilityTable;
    }

    // Add significant variables
    private static void addSignificantVariables(ArrayList<Variable> variablesList, String variable, String[][] evidenceValuePairs) {
        // Create a list of variables
        ArrayList<Variable> alsoSignificant = new ArrayList<Variable>();


        variablesList.add(BayesianNetworkManager.getInstance().getVariable(variable));
        for (String[] evidenceValuePair : evidenceValuePairs) {
            variablesList.add(BayesianNetworkManager.getInstance().getVariable(evidenceValuePair[0]));
        }

        // Add all the ascending parents of the variables
        for (Variable var : variablesList) {
            addAscendingParents(variablesList, alsoSignificant, var);
        }

        // Add the alsoSignificant variables to the variablesList
        variablesList.addAll(alsoSignificant);
    }

    // Add all the ascending parents of the variable
    private static void addAscendingParents(ArrayList<Variable> variablesList, ArrayList<Variable>  alsoSignificant, Variable variable) {
        System.out.println(variable.getName());

        // Go through all the parents of the variable if exist
        if (variable.getParents() != null) {
            for (Variable parent : variable.getParents()) {
                if (!variablesList.contains(parent) && !alsoSignificant.contains(parent)) {
                    alsoSignificant.add(parent);
                }
                addAscendingParents(variablesList, alsoSignificant, parent);
            }
        }
    }

}