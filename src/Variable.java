
public class Variable {

    // Name of variable
    private String name;

    // Array of possible values for variable
    private String[] possibleValues;

    // Array of parents of variable
    private Variable[] parents = null;

    // Probability table for variable
    private double[] probabilityTable;

    // Constructor
    public Variable() {
    }

    // Get name of variable
    public String getName() {
        return name;
    }

    // Set name of variable
    public void setName(String name) {
        this.name = name;
    }

    // Set possible values for variable
    public void setPossibleValues(String[] possibleValues) {
        this.possibleValues = possibleValues;
    }

    // Get possible values for variable
    public String[] getPossibleValues() {
        return possibleValues;
    }

    // Set parents of variable
    public void setParents(Variable[] parents) {
        this.parents = parents;
    }

    // Get parents of variable
    public Variable[] getParents() {
        return parents;
    }

    // Set probability table for variable
    public void setProbabilityTable(double[] probabilityTable) {
        this.probabilityTable = probabilityTable;
    }

    // Get probability table for variable
    public double[] getProbabilityTable() {
        return probabilityTable;
    }



}
