
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
        if(PCTLength>0 && variable.getParents() != null) {

            validPCT = true;
            // Set factor probability
            PCTProbability = variable.getProbabilityTable();

//            System.out.println("Factor length: " + factorLength);
//            System.out.println("Factor probability length: " + factorProbability.length);

            // Create the factor table
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
    }

    // Constructor for the PCT table
    public PCT(Variable[] variablesOrder, double[] PCTProbability){

        // Check if the PCT is valid
        if(!(variablesOrder.length == 0 || PCTProbability.length == 0)){
            // Get the variables order
            this.variablesOrder = new String[variablesOrder.length];
            for (int i = 0; i < variablesOrder.length; i++) {
                this.variablesOrder[i] = variablesOrder[i].getName();
            }

            this.PCTProbability = PCTProbability;
            this.PCTLength = PCTProbability.length;
            this.validPCT = true;

            // Create the PCT table
            PCTTable = new String[variablesOrder.length][PCTLength];

            // Fill the PCT table

            // The possible values length of the last variable
            int counter = 1;

            for (int i = variablesOrder.length - 1; i >= 0; i--) {
                for (int j = 0; j < PCTLength; j++) {
                    PCTTable[i][j] = variablesOrder[i].getPossibleValues()[(j / counter) % variablesOrder[i].getPossibleValues().length];
                }

                counter *= variablesOrder[i].getPossibleValues().length;
            }
        }
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
}
