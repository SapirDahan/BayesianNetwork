import java.util.*;


public class PCT {


    // The PCT table (as 2D array of the variables names and their values)
    private String[][] PCTTable;

    // Variables order
    private String[] variablesOrder;

    // PCT length
    private int PCTLength;

    // PCT probability in order of the factor table
    private double[] PCTProbability;

    // Valid PCT
    private boolean validPCT = false;

    // Constructor that gets the PCT table and the variables names
    public PCT(Variable variable) {

        // Set the factor length
        PCTLength = variable.getProbabilityTable().length;
        if(PCTLength > 0) {

            validPCT = true;

            // Set factor probability
            PCTProbability = variable.getProbabilityTable();

            // Create the factor table
            if(variable.getParents() != null){
                PCTTable = new String[variable.getParents().length + 1][PCTLength];
                // Set the factor table

                // Set the variable values on the last column
                int counter = variable.getPossibleValues().length;
                for (int i = 0; i < PCTLength; i++) {
                    PCTTable[variable.getParents().length][i] = variable.getPossibleValues()[i % counter];
                }

                // Set the parents values
                for (int i = variable.getParents().length - 1; i >= 0; i--) {
                    for (int j = 0; j < PCTLength; j++) {
                        PCTTable[i][j] = variable.getParents()[i].getPossibleValues()[(j / counter) % variable.getParents()[i].getPossibleValues().length];
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
                PCTTable = new String[1][PCTLength];

                // Set the factor table

                // Set the variable values on the last column
                int counter = variable.getPossibleValues().length;
                for (int i = 0; i < PCTLength; i++) {
                    PCTTable[0][i] = variable.getPossibleValues()[i % counter];
                }


                // Set the variables order
                variablesOrder = new String[1];
                variablesOrder[0] = variable.getName();

            }



        }
    }

    public PCT(PCT pct, Variable variable, String condition) {
        // Set the factor length

        PCTLength = pct.getPCTLength()/variable.getPossibleValues().length;
        if(pct.getPCTLength() > 0) {

            validPCT = true;


            // Set factor probability
            PCTProbability = new double[PCTLength];


            // Set the factor table

            // get the index of the condition
            int conditionIndex = -1;
            for (int i = 0; i < pct.getVariablesOrder().length; i++) {
                if(pct.getVariablesOrder()[i].equals(variable.getName())){
                    conditionIndex = i;
                    break;
                }
            }

            if(variable.getPossibleValues().length > 2 || pct.getVariablesOrder().length == 1) {

                // Create the factor table
                PCTTable = new String[pct.getVariablesOrder().length][PCTLength];

                // Copy the pct table while ignoring the condition
                int index = 0;
                for (int i = 0; i < pct.getPCTLength(); i++) {
                    if (pct.getPCTTable()[conditionIndex][i].equals(condition)) {
                        for (int j = 0; j < pct.getVariablesOrder().length; j++) {
                            PCTTable[j][index] = pct.getPCTTable()[j][i];
                        }
                        PCTProbability[index] = pct.getPCTProbability()[i];
                        index++;
                    }
                }

                // Set the variables order
                variablesOrder = pct.getVariablesOrder();
            }

            else{
                // Create the factor table
                PCTTable = new String[pct.getVariablesOrder().length - 1][PCTLength];

                // Copy the pct table while ignoring the condition and the column of the condition
                int index = 0;
                for (int i = 0; i < pct.getPCTLength(); i++) {
                    if (pct.getPCTTable()[conditionIndex][i].equals(condition)) {
                        for (int j = 0; j < pct.getVariablesOrder().length - 1; j++) {
                            if(j < conditionIndex){
                                PCTTable[j][index] = pct.getPCTTable()[j][i];
                            }
                            else{
                                PCTTable[j][index] = pct.getPCTTable()[j+1][i];
                            }
                        }
                        PCTProbability[index] = pct.getPCTProbability()[i];
                        index++;
                    }
                }

                // Set the variables order without the variable
                variablesOrder = new String[pct.getVariablesOrder().length-1];
                index = 0;
                for (int i = 0; i < pct.getVariablesOrder().length; i++) {
                    if(i != conditionIndex){
                        variablesOrder[index] = pct.getVariablesOrder()[i];
                        index++;
                    }
                }
            }
        }
    }

    // Join two PCTs
    public PCT(PCT pct1, PCT pct2) {
        // Find common variables
        List<String> commonVariables = new ArrayList<>();
        for (String var1 : pct1.getVariablesOrder()) {
            for (String var2 : pct2.getVariablesOrder()) {
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

        for (int i = 0; i < pct1.getPCTLength(); i++) {
            for (int j = 0; j < pct2.getPCTLength(); j++) {

                boolean match = true;
                for (String commonVar : commonVariables) {
                    int index1 = Arrays.asList(pct1.getVariablesOrder()).indexOf(commonVar);
                    int index2 = Arrays.asList(pct2.getVariablesOrder()).indexOf(commonVar);

                    if (!pct1.getPCTTable()[index1][i].equals(pct2.getPCTTable()[index2][j])) {
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

        PCTLength = newLength;
        PCTTable = new String[pct1.getVariablesOrder().length + pct2.getVariablesOrder().length - commonVariables.size()][PCTLength];
        PCTProbability = new double[PCTLength];
        System.out.println("PCTLength: " + newLength);

        int rowIndex = 0;
        for (int i = 0; i < pct1.getPCTLength(); i++) {
            for (int j = 0; j < pct2.getPCTLength(); j++) {
                boolean match = true;
                for (String commonVar : commonVariables) {
                    int index1 = Arrays.asList(pct1.getVariablesOrder()).indexOf(commonVar);
                    int index2 = Arrays.asList(pct2.getVariablesOrder()).indexOf(commonVar);

                    if (!pct1.getPCTTable()[index1][i].equals(pct2.getPCTTable()[index2][j])) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    int colIndex = 0;
                    for (int k = 0; k < pct1.getVariablesOrder().length; k++) {
                        if (!commonVariables.contains(pct1.getVariablesOrder()[k])) {
                            PCTTable[colIndex++][rowIndex] = pct1.getPCTTable()[k][i];
                        }
                    }
                    for (int k = 0; k < pct2.getVariablesOrder().length; k++) {
                        if (!commonVariables.contains(pct2.getVariablesOrder()[k])) {
                            PCTTable[colIndex++][rowIndex] = pct2.getPCTTable()[k][j];
                        }
                    }
                    for (String commonVar : commonVariables) {
                        int index = Arrays.asList(pct1.getVariablesOrder()).indexOf(commonVar);
                        PCTTable[colIndex++][rowIndex] = pct1.getPCTTable()[index][i];
                    }
                    PCTProbability[rowIndex] = pct1.getPCTProbability()[i] * pct2.getPCTProbability()[j];
                    VariableEliminationLogic.multiplicationOperations++;
                    rowIndex++;
                }
            }
        }

        // Set the variables order
        variablesOrder = new String[pct1.getVariablesOrder().length + pct2.getVariablesOrder().length - commonVariables.size()];
        int orderIndex = 0;
        for (String var : pct1.getVariablesOrder()) {
            if (!commonVariables.contains(var)) {
                variablesOrder[orderIndex++] = var;
            }
        }
        for (String var : pct2.getVariablesOrder()) {
            if (!commonVariables.contains(var)) {
                variablesOrder[orderIndex++] = var;
            }
        }
        for (String var : commonVariables) {
            variablesOrder[orderIndex++] = var;
        }

        validPCT = true;
    }

    // Eliminate a variable from the PCT
    public PCT(PCT pct, Variable variable) {
        // Initialize resultPCT structures
        List<String[]> resultPCTTable = new ArrayList<>();
        List<Double> resultPCTProbability = new ArrayList<>();

        // Get the index of the variable to be excluded
        int excludeIndex = -1;
        for (int i = 0; i < pct.variablesOrder.length; i++) {
            if (pct.variablesOrder[i].equals(variable.getName())) {
                excludeIndex = i;
                break;
            }
        }

        if (excludeIndex == -1) {
            throw new IllegalArgumentException("Variable not found in the PCT");
        }

        // Create a list to keep track of which rows have been used
        boolean[] usedRows = new boolean[pct.PCTLength];

        for (int i = 0; i < pct.PCTLength - 1; i++) {
            if (usedRows[i]) {
                continue;
            }

            String[] baseRow = new String[pct.getVariablesOrder().length];
            for (int k = 0; k < pct.getVariablesOrder().length; k++) {
                baseRow[k] = pct.PCTTable[k][i];
            }

            double probabilitySum = pct.PCTProbability[i];
            usedRows[i] = true;

            for (int j = i + 1; j < pct.PCTLength; j++) {
                if (usedRows[j]) {
                    continue;
                }

                boolean identical = true;
                for (int k = 0; k < pct.getVariablesOrder().length; k++) {
//                    System.out.println("i: " + i);
//                    System.out.println("j: " + j);
//                    System.out.println("k: " + k);
//                    System.out.println("condition: " + baseRow[k].equals(pct.PCTTable[k][j]));
//                    System.out.println("usedRows: " + Arrays.toString(usedRows));
//
//
//                    System.out.println("-----------------");
                    if (k != excludeIndex && !baseRow[k].equals(pct.PCTTable[k][j])) {

                        identical = false;
                        break;
                    }


                }

                if (identical) {
                    probabilitySum += pct.PCTProbability[j];
                    VariableEliminationLogic.additionOperations++;
                    usedRows[j] = true;
                }

            }

            // Create the new row excluding the specified variable
            String[] newRow = new String[pct.getVariablesOrder().length - 1];
            int newIndex = 0;
            for (int k = 0; k < baseRow.length; k++) {
                if (k != excludeIndex) {
                    newRow[newIndex++] = baseRow[k];
                }
            }

            resultPCTTable.add(newRow);
            resultPCTProbability.add(probabilitySum);
        }

        // Convert the lists to arrays
        PCTTable = new String[pct.getVariablesOrder().length - 1][resultPCTTable.size()];
        for (int j = 0; j < PCTTable[0].length; j++) {
            for (int i = 0; i < PCTTable.length; i++) {
                PCTTable[i][j] = resultPCTTable.get(j)[i];
            }
        }


        PCTProbability = new double[resultPCTProbability.size()];
        for (int i = 0; i < resultPCTProbability.size(); i++) {
            PCTProbability[i] = resultPCTProbability.get(i);
        }

        PCTLength = PCTTable[0].length;

        // Set the new variables order
        variablesOrder = new String[pct.variablesOrder.length - 1];
        int index = 0;
        for (int i = 0; i < pct.variablesOrder.length; i++) {
            if (i != excludeIndex) {
                variablesOrder[index++] = pct.variablesOrder[i];
            }
        }

        validPCT = true;
    }



    // Get the value from the factor table
    public double getValue(String[] value, String[][] evidence){
        if(validPCT){

            // Arrange the evidence and the value in the same order as the factor table
            String[] tableRow = new String[PCTTable.length];
            for (String[] strings : evidence) {
                for (int j = 0; j < PCTTable.length; j++) {
                    if (strings[0].equals(variablesOrder[j])) {
                        tableRow[j] = strings[1];
                        break;
                    }
                }
            }

            tableRow[PCTTable.length - 1] = value[1];

            // Check if the array is full
            for (String s : tableRow) {
                if (s == null) {
                    return -1;
                }
            }

            // Find the index of the value in the factor table
            for(int i = 0; i < PCTTable[0].length; i++){
                boolean flag = true;
                for(int j = 0; j < PCTTable.length; j++){
                    if(!tableRow[j].equals(PCTTable[j][i])){
                       flag = false;
                    }
                }
                if(flag){
                    return PCTProbability[i];
                }
            }
        }

        // If the PCT is not valid or the value is not in the table
        return -1;
    }

    // Get the PCT table
    public String[][] getPCTTable() {
        return PCTTable;
    }

    // Get the variables order
    public String[] getVariablesOrder() {
        return variablesOrder;
    }

    // Get the PCT length
    public int getPCTLength() {
        return PCTLength;
    }

    // Get the PCT probability
    public double[] getPCTProbability() {
        return PCTProbability;
    }

    // Check if the PCT is valid
    public boolean isValidPCT() {
        return validPCT;
    }

    public boolean containsVariable(String variable) {
        for (String s : variablesOrder) {
            if (s.equals(variable)) {
                return true;
            }
        }
        return false;
    }

    // Return the variables in order of the PCT table
    public Variable[] getVariablesInOrder(){
        Variable[] variables = new Variable[variablesOrder.length];
        for(int i = 0; i < variablesOrder.length; i++){
            variables[i] = BayesianNetworkManager.getInstance().getVariables().get(variablesOrder[i]);
        }
        return variables;
    }

    // Set the PCT probability table
    public void setPCTProbability(double[] PCTProbability) {
        this.PCTProbability = PCTProbability;
    }

}
