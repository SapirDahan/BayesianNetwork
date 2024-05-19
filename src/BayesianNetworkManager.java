import java.util.HashMap;

public class BayesianNetworkManager {

    // Singleton instance
    private static BayesianNetworkManager instance = null;

    // Hashmap to store variables
    private HashMap<String, Variable> variables = new HashMap<String, Variable>();


    // Private constructor for singleton class
    private BayesianNetworkManager() {}

    // Get instance of singleton class
    public static BayesianNetworkManager getInstance() {
        if (instance == null) {
            instance = new BayesianNetworkManager();
        }
        return instance;
    }

    // Add variable to network
    public void addVariable(Variable variable) {
        variables.put(variable.getName(), variable);
    }

    // Get variable from network
    public Variable getVariable(String name) {
        return variables.get(name);
    }

    // Get variables from network
    public HashMap<String, Variable> getVariables() {
        return variables;
    }

    // Go through all the variables and set the children
    public void setChildren() {
//        for(int i = 0; i < variables.size(); i++){
//            Variable variable = variables.get(i);
//            for(int j = 0; j < variable.getParents().length; j++){
//                variable.getParents()[j].addChild(variable);
//            }
//        }
        for (Variable variable : variables.values()) {
            if(variable.getParents() != null){
                for (Variable parent : variable.getParents()) {
                    parent.addChild(variable);
                }
            }
        }

    }

}
