public class Factor {

    // The factor table (as 2D array of the variables names and their values)
    private String[][] factorTable;

    // Variables order
    private String[] variablesOrder;

    // Factor length
    private int factorLength;

    // Factor probability in order of the factor table
    private double[] factorProbability;

    // Constructor that gets the factor table and the variables names
    public Factor(Variable variable) {
        // Set the factor length
        factorLength = variable.getProbabilityTable().length;

        // Set factor probability
        factorProbability = variable.getProbabilityTable();

        System.out.println("Factor length: " + factorLength);
        System.out.println("Factor probability length: " + factorProbability.length);

        // Create the factor table
        if(factorLength>0 && variable.getParents() != null){
            factorTable = new String[variable.getParents().length + 1][factorLength];
        }


        // Set the factor table

        // Set the variable values on the last column
        int counter = variable.getPossibleValues().length;
        for(int i = 0; i < factorLength; i++){
            factorTable[variable.getParents().length][i] = variable.getPossibleValues()[i % counter];
        }

        // Set the parents values
        for(int i = variable.getParents().length - 1; i >= 0; i--){
            for(int j = 0; j < factorLength; j++){
                factorTable[i][j] = variable.getParents()[i].getPossibleValues()[(j / counter) % variable.getParents()[i].getPossibleValues().length];
            }

            counter *= variable.getParents()[i].getPossibleValues().length;
        }

        // Set the variables order
        variablesOrder = new String[variable.getParents().length + 1];
        variablesOrder[variable.getParents().length] = variable.getName();
        for(int i = 0; i < variable.getParents().length; i++){
            variablesOrder[i] = variable.getParents()[i].getName();
        }

    }

    // Get the value from the factor table
    public double getValue(String[] values, String[][] evidence){



        return factorProbability[index];
    }
}
