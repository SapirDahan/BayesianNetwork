
public class Variable {

    // Name of variable
    private String name;

    // Array of possible values for variable
    private String[] possibleValues;

    // Array of parents of variable
    private Variable[] parents = null;

    // Array of children of variable
    private Variable[] children = null;

    // Probability table for variable
    private double[] probabilityTable;

    // For variable elimination:
    final static int UNCOLORED = 0;
    final static int COLORED = 1;
    final static int UNVISITED = 0;
    final static int VISIT_FROM_CHILD = 1;
    final static int VISIT_FROM_PARENT = 2;

    // At first the variable is uncolored and unvisited
    int color = UNCOLORED;
    int visited = UNVISITED;

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

    // Set children of variable
    public void setChildren(Variable[] children) {
        this.children = children;
    }

    // Get children of variable
    public Variable[] getChildren() {
        return children;
    }

    // Add child to variable
    public void addChild(Variable child) {
        if (children == null) {
            children = new Variable[1];
            children[0] = child;
        }

        else {
            Variable[] newChildren = new Variable[children.length + 1];
            for (int i = 0; i < children.length; i++) {
                newChildren[i] = children[i];
            }
            newChildren[children.length] = child;
            children = newChildren;
        }
    }

    // Set probability table for variable
    public void setProbabilityTable(double[] probabilityTable) {
        this.probabilityTable = probabilityTable;
    }

    // Get probability table for variable
    public double[] getProbabilityTable() {
        return probabilityTable;
    }

    // Has parents
    public boolean hasParents() {
        return parents != null;
    }

    // Has children
    public boolean hasChildren() {
        return children != null;
    }


}
