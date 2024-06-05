import java.util.*;


public class Factor {


    // The Factor table (as 2D array of the variables names and their values)
    private String[][] FactorTable;

    // Variables order
    private String[] variablesOrder;

    // Factor length
    private int FactorLength;

    // Factor probability in order of the factor table
    private double[] FactorProbability;

    // Constructor that gets the Factor table and the variables names
    public Factor(Variable variable) {

        // Set the factor length
        FactorLength = variable.getProbabilityTable().length;

        // Set factor probability
        FactorProbability = variable.getProbabilityTable();

        // Create the factor table
        if(variable.getParents() != null){
            FactorTable = new String[variable.getParents().length + 1][FactorLength];
            // Set the factor table

            // Set the variable values on the last column
            int counter = variable.getPossibleValues().length;
            for (int i = 0; i < FactorLength; i++) {
                FactorTable[variable.getParents().length][i] = variable.getPossibleValues()[i % counter];
            }

            // Set the parents values
            for (int i = variable.getParents().length - 1; i >= 0; i--) {
                for (int j = 0; j < FactorLength; j++) {
                    FactorTable[i][j] = variable.getParents()[i].getPossibleValues()[(j / counter) % variable.getParents()[i].getPossibleValues().length];
                }

                counter *= variable.getParents()[i].getPossibleValues().length;
            }

            // Set the variables order
            variablesOrder = new String[variable.getParents().length + 1];
            variablesOrder[variable.getParents().length] = variable.getName();
            for (int i = 0; i < variable.getParents().length; i++) {
                variablesOrder[i] = variable.getParents()[i].getName();
            }
        }

        else{
            FactorTable = new String[1][FactorLength];

            // Set the factor table

            // Set the variable values on the last column
            int counter = variable.getPossibleValues().length;
            for (int i = 0; i < FactorLength; i++) {
                FactorTable[0][i] = variable.getPossibleValues()[i % counter];
            }


            // Set the variables order
            variablesOrder = new String[1];
            variablesOrder[0] = variable.getName();

        }

    }

    public Factor(Factor factor, Variable variable, String condition) {
        // Set the factor length

        FactorLength = factor.getFactorLength()/variable.getPossibleValues().length;
        if(factor.getFactorLength() > 0) {

            // Set factor probability
            FactorProbability = new double[FactorLength];


            // Set the factor table

            // get the index of the condition
            int conditionIndex = -1;
            for (int i = 0; i < factor.getVariablesOrder().length; i++) {
                if(factor.getVariablesOrder()[i].equals(variable.getName())){
                    conditionIndex = i;
                    break;
                }
            }

            if(variable.getPossibleValues().length > 2 || factor.getVariablesOrder().length == 1) {

                // Create the factor table
                FactorTable = new String[factor.getVariablesOrder().length][FactorLength];

                // Copy the Factor table while ignoring the condition
                int index = 0;
                for (int i = 0; i < factor.getFactorLength(); i++) {
                    if (factor.getFactorTable()[conditionIndex][i].equals(condition)) {
                        for (int j = 0; j < factor.getVariablesOrder().length; j++) {
                            FactorTable[j][index] = factor.getFactorTable()[j][i];
                        }
                        FactorProbability[index] = factor.getFactorProbability()[i];
                        index++;
                    }
                }

                // Set the variables order
                variablesOrder = factor.getVariablesOrder();
            }

            else{
                // Create the factor table
                FactorTable = new String[factor.getVariablesOrder().length - 1][FactorLength];

                // Copy the Factor table while ignoring the condition and the column of the condition
                int index = 0;
                for (int i = 0; i < factor.getFactorLength(); i++) {
                    if (factor.getFactorTable()[conditionIndex][i].equals(condition)) {
                        for (int j = 0; j < factor.getVariablesOrder().length - 1; j++) {
                            if(j < conditionIndex){
                                FactorTable[j][index] = factor.getFactorTable()[j][i];
                            }
                            else{
                                FactorTable[j][index] = factor.getFactorTable()[j+1][i];
                            }
                        }
                        FactorProbability[index] = factor.getFactorProbability()[i];
                        index++;
                    }
                }

                // Set the variables order without the variable
                variablesOrder = new String[factor.getVariablesOrder().length-1];
                index = 0;
                for (int i = 0; i < factor.getVariablesOrder().length; i++) {
                    if(i != conditionIndex){
                        variablesOrder[index] = factor.getVariablesOrder()[i];
                        index++;
                    }
                }
            }
        }
    }

    // Join two Factors
    public Factor(Factor factor1, Factor factor2) {
        // Find common variables
        List<String> commonVariables = new ArrayList<>();
        for (String var1 : factor1.getVariablesOrder()) {
            for (String var2 : factor2.getVariablesOrder()) {
                if (var1.equals(var2)) {
                    commonVariables.add(var1);
                    break;
                }
            }
        }

        //System.out.println("Common variables: " + commonVariables.toString());

        // Calculate the new table length
        int newLength = 0;
        List<Integer> matchingIndices1 = new ArrayList<>();
        List<Integer> matchingIndices2 = new ArrayList<>();

        for (int i = 0; i < factor1.getFactorLength(); i++) {
            for (int j = 0; j < factor2.getFactorLength(); j++) {

                boolean match = true;
                for (String commonVar : commonVariables) {
                    int index1 = Arrays.asList(factor1.getVariablesOrder()).indexOf(commonVar);
                    int index2 = Arrays.asList(factor2.getVariablesOrder()).indexOf(commonVar);

                    if (!factor1.getFactorTable()[index1][i].equals(factor2.getFactorTable()[index2][j])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    newLength++;
                    matchingIndices1.add(i);
                    matchingIndices2.add(j);
                }
            }
        }

        FactorLength = newLength;
        FactorTable = new String[factor1.getVariablesOrder().length + factor2.getVariablesOrder().length - commonVariables.size()][FactorLength];
        FactorProbability = new double[FactorLength];

        int rowIndex = 0;
        for (int i = 0; i < factor1.getFactorLength(); i++) {
            for (int j = 0; j < factor2.getFactorLength(); j++) {
                boolean match = true;
                for (String commonVar : commonVariables) {
                    int index1 = Arrays.asList(factor1.getVariablesOrder()).indexOf(commonVar);
                    int index2 = Arrays.asList(factor2.getVariablesOrder()).indexOf(commonVar);

                    if (!factor1.getFactorTable()[index1][i].equals(factor2.getFactorTable()[index2][j])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int colIndex = 0;
                    for (int k = 0; k < factor1.getVariablesOrder().length; k++) {
                        if (!commonVariables.contains(factor1.getVariablesOrder()[k])) {
                            FactorTable[colIndex++][rowIndex] = factor1.getFactorTable()[k][i];
                        }
                    }
                    for (int k = 0; k < factor2.getVariablesOrder().length; k++) {
                        if (!commonVariables.contains(factor2.getVariablesOrder()[k])) {
                            FactorTable[colIndex++][rowIndex] = factor2.getFactorTable()[k][j];
                        }
                    }
                    for (String commonVar : commonVariables) {
                        int index = Arrays.asList(factor1.getVariablesOrder()).indexOf(commonVar);
                        FactorTable[colIndex++][rowIndex] = factor1.getFactorTable()[index][i];
                    }
                    FactorProbability[rowIndex] = factor1.getFactorProbability()[i] * factor2.getFactorProbability()[j];
                    VariableEliminationLogic.multiplicationOperations++;
                    rowIndex++;
                }
            }
        }

        // Set the variables order
        variablesOrder = new String[factor1.getVariablesOrder().length + factor2.getVariablesOrder().length - commonVariables.size()];
        int orderIndex = 0;
        for (String var : factor1.getVariablesOrder()) {
            if (!commonVariables.contains(var)) {
                variablesOrder[orderIndex++] = var;
            }
        }
        for (String var : factor2.getVariablesOrder()) {
            if (!commonVariables.contains(var)) {
                variablesOrder[orderIndex++] = var;
            }
        }
        for (String var : commonVariables) {
            variablesOrder[orderIndex++] = var;
        }

    }

    // Eliminate a variable from the Factor
    public Factor(Factor factor, Variable variable) {
        // Initialize resultFactor structures
        List<String[]> resultFactorTable = new ArrayList<>();
        List<Double> resultFactorProbability = new ArrayList<>();

        // Get the index of the variable to be excluded
        int excludeIndex = -1;
        for (int i = 0; i < factor.variablesOrder.length; i++) {
            if (factor.variablesOrder[i].equals(variable.getName())) {
                excludeIndex = i;
                break;
            }
        }

        if (excludeIndex == -1) {
            throw new IllegalArgumentException("Variable not found in the Factor");
        }

        // Create a list to keep track of which rows have been used
        boolean[] usedRows = new boolean[factor.FactorLength];

        for (int i = 0; i < factor.FactorLength - 1; i++) {
            if (usedRows[i]) {
                continue;
            }

            String[] baseRow = new String[factor.getVariablesOrder().length];
            for (int k = 0; k < factor.getVariablesOrder().length; k++) {
                baseRow[k] = factor.FactorTable[k][i];
            }

            double probabilitySum = factor.FactorProbability[i];
            usedRows[i] = true;

            for (int j = i + 1; j < factor.FactorLength; j++) {
                if (usedRows[j]) {
                    continue;
                }

                boolean identical = true;
                for (int k = 0; k < factor.getVariablesOrder().length; k++) {

                    if (k != excludeIndex && !baseRow[k].equals(factor.FactorTable[k][j])) {

                        identical = false;
                        break;
                    }


                }

                if (identical) {
                    probabilitySum += factor.FactorProbability[j];
                    VariableEliminationLogic.additionOperations++;
                    usedRows[j] = true;
                }

            }

            // Create the new row excluding the specified variable
            String[] newRow = new String[factor.getVariablesOrder().length - 1];
            int newIndex = 0;
            for (int k = 0; k < baseRow.length; k++) {
                if (k != excludeIndex) {
                    newRow[newIndex++] = baseRow[k];
                }
            }

            resultFactorTable.add(newRow);
            resultFactorProbability.add(probabilitySum);
        }

        // Convert the lists to arrays
        FactorTable = new String[factor.getVariablesOrder().length - 1][resultFactorTable.size()];
        for (int j = 0; j < FactorTable[0].length; j++) {
            for (int i = 0; i < FactorTable.length; i++) {
                FactorTable[i][j] = resultFactorTable.get(j)[i];
            }
        }


        FactorProbability = new double[resultFactorProbability.size()];
        for (int i = 0; i < resultFactorProbability.size(); i++) {
            FactorProbability[i] = resultFactorProbability.get(i);
        }

        FactorLength = FactorTable[0].length;

        // Set the new variables order
        variablesOrder = new String[factor.variablesOrder.length - 1];
        int index = 0;
        for (int i = 0; i < factor.variablesOrder.length; i++) {
            if (i != excludeIndex) {
                variablesOrder[index++] = factor.variablesOrder[i];
            }
        }

    }



    // Get the value from the factor table
    public double getValue(String[] value, String[][] evidence){

        // Arrange the evidence and the value in the same order as the factor table
        String[] tableRow = new String[FactorTable.length];
        for (String[] strings : evidence) {
            for (int j = 0; j < FactorTable.length; j++) {
                if (strings[0].equals(variablesOrder[j])) {
                    tableRow[j] = strings[1];
                    break;
                }
            }
        }

        tableRow[FactorTable.length - 1] = value[1];

        // Check if the array is full
        for (String s : tableRow) {
            if (s == null) {
                return -1;
            }
        }

        // Find the index of the value in the factor table
        for(int i = 0; i < FactorTable[0].length; i++){
            boolean flag = true;
            for(int j = 0; j < FactorTable.length; j++){
                if(!tableRow[j].equals(FactorTable[j][i])){
                   flag = false;
                }
            }
            if(flag){
                return FactorProbability[i];
            }
        }


        // If the Factor is not valid or the value is not in the table
        return -1;
    }

    // Get the Factor table
    public String[][] getFactorTable() {
        return FactorTable;
    }

    // Get the variables order
    public String[] getVariablesOrder() {
        return variablesOrder;
    }

    // Get the Factor length
    public int getFactorLength() {
        return FactorLength;
    }

    // Get the Factor probability
    public double[] getFactorProbability() {
        return FactorProbability;
    }


    public boolean containsVariable(String variable) {
        for (String s : variablesOrder) {
            if (s.equals(variable)) {
                return true;
            }
        }
        return false;
    }

    // Return the variables in order of the Factor table
    public Variable[] getVariablesInOrder(){
        Variable[] variables = new Variable[variablesOrder.length];
        for(int i = 0; i < variablesOrder.length; i++){
            variables[i] = BayesianNetworkManager.getInstance().getVariables().get(variablesOrder[i]);
        }
        return variables;
    }

    // Set the Factor probability table
    public void setFactorProbability(double[] FactorProbability) {
        this.FactorProbability = FactorProbability;
    }

}
