import java.text.DecimalFormat;
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
        String[] evidence;
        if(querySplit.length > 1){
            evidence = querySplit[1].split(",");
        }
        else{
            evidence = null;
        }


        // Save in 2D array the values of the evidence and the evidence, split by "="
        String[][] evidenceValuePairs;
        if(evidence != null){
            evidenceValuePairs = new String[evidence.length][2];
            for (int i = 0; i < evidence.length; i++) {
                String[] parts = evidence[i].split("=");
                evidenceValuePairs[i][0] = parts[0]; // Left value
                evidenceValuePairs[i][1] = parts[1]; // Right value
            }
        }
        else{
            evidenceValuePairs = null;
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

        // Create a list of Factors for the variables int the network
        ArrayList<Factor> Factors = new ArrayList<Factor>();

        // Create the Factors for the variables in the network
        for (Variable variable : variablesList) {
            Factor factor = new Factor(variable);
            Factors.add(factor);
        }


        // interate over all Factors then iterate over all evidence values and creates for each Factor a new Factor where the evidence is met then add the new Factor to the Factors list and remove the old one
        if(evidenceValuePairs != null) {
            for (int i = 0; i < Factors.size(); i++) {
                Factor factor = Factors.get(i);
                for (String[] evidenceValuePair : evidenceValuePairs) {
                    if (factor.containsVariable(evidenceValuePair[0])) {
                        factor = new Factor(factor, BayesianNetworkManager.getInstance().getVariable(evidenceValuePair[0]), evidenceValuePair[1]);
                    }
                }
                Factors.set(i, factor);
            }
        }

        // Go through all the Factors and remove the ones that have length 1
        for (int i = 0; i < Factors.size(); i++) {
            if (Factors.get(i).getFactorLength() <= 1) {
                Factors.remove(i);
                i--;
            }
        }


        // Iterate over all hidden variables
        for (String hiddenVariable : hidden) {

            // Get all the Factors that contain the hidden variable
            ArrayList<Factor> FactorsWithVariable = getFactorsContainingVariable(Factors, hiddenVariable);

            // Sort the Factors by the length ascending order and secondary sort by the ascii value ascending order of the variables
            FactorsWithVariable = sortFactors(FactorsWithVariable);

            Factors.removeAll(FactorsWithVariable);



            if(FactorsWithVariable.size() > 0){
                Factor resultFactor = FactorsWithVariable.get(0);
                for (int i = 1; i < FactorsWithVariable.size(); i++) {
                    resultFactor = new Factor(resultFactor, FactorsWithVariable.get(i));

                }

                resultFactor = new Factor(resultFactor, variables.get(hiddenVariable));

                // Append the resulting Factor to Factors
                Factors.add(resultFactor);

            }

        }


        // Get all the Factors that contain the hidden variable
        ArrayList<Factor> FactorsWithVariable = getFactorsContainingVariable(Factors, variableValuePair[0]);

        // Sort the Factors by the length ascending order and secondary sort by the ascii value ascending order of the variables
        FactorsWithVariable = sortFactors(FactorsWithVariable);

        Factor resultFactor = FactorsWithVariable.get(0);
        for (int i = 1; i < FactorsWithVariable.size(); i++) {
            resultFactor = new Factor(resultFactor, FactorsWithVariable.get(i));

        }

        // Normalize the probability table
        double[] probabilityTable = normalizeProbabilityTable(resultFactor.getFactorProbability());

        // Set the probability table of the resultFactor
        resultFactor.setFactorProbability(probabilityTable);


        // Calculate the result

        // Get the index of the variable in the FactorTable
        int index = 0;
        for(int i = 0; i < resultFactor.getVariablesOrder().length; i++){
            if(resultFactor.getVariablesOrder()[i].equals(variableValuePair[0])){
                index = i;
            }
        }
        for (int i = 0; i < probabilityTable.length; i++) {
            if (resultFactor.getFactorTable()[index][i].equals(variableValuePair[1])) {
                result = probabilityTable[i];
                break;
            }
        }

        // Return the result, formatted to 5 decimal places, and the number of addition and multiplication operations
        return formatResult(result);
    }

    private static void resetOperationCounts() {
        additionOperations = 0;
        multiplicationOperations = 0;
    }

    private static String formatResult(double result) {
        DecimalFormat df = new DecimalFormat("#.#####");
        return df.format(result) + "," + additionOperations + "," + multiplicationOperations;
    }


    // Get ascii value of a Factors variables
    private static int getAsciiValue(String[] variables) {
        int asciiValue = 0;
        for (String variable : variables) {
            for (int i = 0; i < variable.length(); i++) {
                asciiValue += (int) variable.charAt(i);
            }
        }
        return asciiValue;
    }

    // Sort the Factors by the length ascending order and secondary sort by the ascii value ascending order of the variables
    private static ArrayList<Factor> sortFactors(ArrayList<Factor> Factors) {
        Collections.sort(Factors, new Comparator<Factor>() {
            @Override
            public int compare(Factor factor1, Factor factor2) {
                if (factor1.getFactorLength() == factor2.getFactorLength()) {
                    return getAsciiValue(factor1.getVariablesOrder()) - getAsciiValue(factor2.getVariablesOrder());
                }
                return factor1.getFactorLength() - factor2.getFactorLength();
            }
        });
        return Factors;
    }


    // Get arrayList of Factors and return all the Factors that contain the variable
    private static ArrayList<Factor> getFactorsContainingVariable(ArrayList<Factor> Factors, String variable) {
        ArrayList<Factor> FactorsWithVariable = new ArrayList<Factor>();
        for (Factor factor : Factors) {
            if (factor.containsVariable(variable)) {
                FactorsWithVariable.add(factor);
            }
        }
        return FactorsWithVariable;
    }

    // Normalize the probability table
    private static double[] normalizeProbabilityTable(double[] probabilityTable) {

        // Normalize only if we had done calculations
        if(multiplicationOperations != 0) {
            double sum = 0.0;
            for (double probability : probabilityTable) {
                sum += probability;
            }
            additionOperations++;
            for (int i = 0; i < probabilityTable.length; i++) {
                probabilityTable[i] = probabilityTable[i] / sum;
            }
        }

        return probabilityTable;
    }

    // Add significant variables
    private static void addSignificantVariables(ArrayList<Variable> variablesList, String variable, String[][] evidenceValuePairs) {
        // Create a list of variables
        ArrayList<Variable> alsoSignificant = new ArrayList<Variable>();


        variablesList.add(BayesianNetworkManager.getInstance().getVariable(variable));
        if(evidenceValuePairs != null){
            for (String[] evidenceValuePair : evidenceValuePairs) {
                variablesList.add(BayesianNetworkManager.getInstance().getVariable(evidenceValuePair[0]));
            }
        }


        // Add all the ascending parents of the variables
        for (Variable var : variablesList) {
            addAscendingParents(variablesList, alsoSignificant, var);
        }

        // Add the alsoSignificant variables to the variablesList
        variablesList.addAll(alsoSignificant);

        // Create an array of Strings of the evidence variables without the variable
        String[] evidenceVariables = null;
        if(evidenceValuePairs != null) {
            evidenceVariables = new String[evidenceValuePairs.length];
            for (int i = 0; i < evidenceValuePairs.length; i++) {
                evidenceVariables[i] = evidenceValuePairs[i][0];
            }
        }

        // From the alsoSignificant list, remove the variables that are independent of the variable given the evidence
        if(!alsoSignificant.isEmpty()){
            for (Variable var : alsoSignificant) {
                if (evidenceVariables != null) {
                    if (BayesBallLogic.areVariablesIndependent(variable, var.getName(), evidenceVariables)) {
                        variablesList.remove(var);
                    }
                }
                else {
                    if (BayesBallLogic.areVariablesIndependent(variable, var.getName(), null)) {
                        variablesList.remove(var);
                    }
                }
            }
        }
    }

    // Add all the ascending parents of the variable
    private static void addAscendingParents(ArrayList<Variable> variablesList, ArrayList<Variable>  alsoSignificant, Variable variable) {

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