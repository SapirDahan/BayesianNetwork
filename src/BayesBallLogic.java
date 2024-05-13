//
//public class BayesBallLogic {
//
//    // Perform Bayes Ball
//
//    // To go over @@@
//    public static boolean performBayesBall(String line) {
//
//        // Split the line to get the variables
//        String[] variables = line.substring(2, line.length() - 1).split("\\|");
//
//        // Get the source variable
//        String sourceVariable = variables[0];
//
//        // Get the destination variable
//        String destinationVariable = variables[1];
//
//        // Get the evidence variables
//        String[] evidenceVariables = variables[2].split(",");
//
//        // Get the Bayesian network
//        BayesianNetworkManager network = BayesianNetworkManager.getInstance();
//
//        // Get the source node
//        BayesianNode sourceNode = network.getNode(sourceVariable);
//
//        // Get the destination node
//        BayesianNode destinationNode = network.getNode(destinationVariable);
//
//        // Get the evidence nodes
//        BayesianNode[] evidenceNodes = new BayesianNode[evidenceVariables.length];
//
//        for (int i = 0; i < evidenceVariables.length; i++) {
//            evidenceNodes[i] = network.getNode(evidenceVariables[i]);
//        }
//
//        // Perform Bayes Ball
//        boolean isIndependent = BayesBall.isIndependent(sourceNode, destinationNode, evidenceNodes);
//
//        // Print the result
//        if (isIndependent) {
//            System.out.println("Independent");
//        } else {
//            System.out.println("Dependent");
//        }
//
//    }
//
//
//}
