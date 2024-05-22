import java.util.*;

public class BayesBallLogic {

    // Perform Bayes Ball
    public static boolean performBayesBall(String line) {
        // Split the line by the "|" character
        String[] splitLine = line.split("\\|");

        // Get the variables in the query
        String[] vals = splitLine[0].split("-");
        String var1 = vals[0];
        String var2 = vals[1];

        String[] evidences = null;
        if (splitLine.length > 1) {
            // Get the variables in the evidence
            String evidence = splitLine[1];
            evidences = evidence.split(",");

            // In evidence, remove everything after the equals sign
            for (int i = 0; i < evidences.length; i++) {
                String[] evidenceSplit = evidences[i].split("=");
                evidences[i] = evidenceSplit[0];
            }
        }


        // Determine if the variables are independent
        return areVariablesIndependent(var1, var2, evidences);
    }

    // Determine if the variables are independent
    private static boolean areVariablesIndependent(String var1, String var2, String[] evidences) {
        // If evidence is not empty, color the given nodes
        if (evidences != null) {
            colorGivenVariables(evidences);
        }

        // Get the variables of the network
        HashMap<String, Variable> vars = BayesianNetworkManager.getInstance().getVariables();

        return searchIfReached(vars.get(var1), vars.get(var2), vars);
    }

    private static boolean searchIfReached(Variable source, Variable target, HashMap<String, Variable> vars){
        Queue<Variable> toVisit = new LinkedList<>();
        Variable current = source;
        source.visited = Variable.VISIT_FROM_CHILD;
        toVisit.add(current);

        // Perform BFS
        while (!toVisit.isEmpty()) {

            // Get the current variable and remove it from the queue
            current = toVisit.remove();

            // If the target is reached, return false, because the variables are dependent
            if (current.getName().equals(target.getName())) {
                resetGivenNodes();
                return false;
            }

            // If the variable is uncolored and visited from a child, visit the children and parents
            if (current.color == Variable.UNCOLORED && current.visited == Variable.VISIT_FROM_CHILD) {
                if (current.hasChildren()) {
                    for (Variable child : current.getChildren()) {
                        if (child.visited == Variable.UNVISITED) {
                            child.visited = Variable.VISIT_FROM_PARENT;
                            toVisit.add(child);
                        }
                    }
                }

                if (current.hasParents()) {
                    for (Variable parent : current.getParents()) {
                        if (parent.visited == Variable.UNVISITED) {
                            parent.visited = Variable.VISIT_FROM_CHILD;
                            toVisit.add(parent);
                        }
                    }
                }
            }

            // If the variable is uncolored and visited from a parent, visit the children
            else if (current.color == Variable.UNCOLORED && current.visited == Variable.VISIT_FROM_PARENT) {
                if (current.hasChildren()) {
                    for (Variable child : current.getChildren()) {
                        if (child.visited != Variable.VISIT_FROM_PARENT) {
                            child.visited = Variable.VISIT_FROM_PARENT;
                            toVisit.add(child);
                        }
                    }
                }
            }

            // If the variable is colored and visited from a parent, visit the parents
            else if (current.color == Variable.COLORED && current.visited == Variable.VISIT_FROM_PARENT) {
                if (current.hasParents()) {
                    for (Variable parent : current.getParents()) {
                        if (parent.visited != Variable.VISIT_FROM_CHILD) {
                            parent.visited = Variable.VISIT_FROM_CHILD;
                            toVisit.add(parent);
                        }
                    }
                }
            }
        }

        // If the target is not reached, return true, because the variables are independent
        resetGivenNodes();
        return true;
    }

    public static void colorGivenVariables(String[] given) {

        // Get the variables of the network
        HashMap<String, Variable> vals = BayesianNetworkManager.getInstance().getVariables();

        // Color the given variables
        for (String s : given) {
            Variable var = vals.get(s);
            var.color = Variable.COLORED;
        }
    }

    public static void resetGivenNodes() {
        // Reset the colored variables
        HashMap<String, Variable> vals = BayesianNetworkManager.getInstance().getVariables();
        for (Variable var : vals.values()) {
            var.color = Variable.UNCOLORED;
        }

        // Reset the visited variables
        for (Variable var : vals.values()) {
            var.visited = Variable.UNVISITED;
        }
    }
}
