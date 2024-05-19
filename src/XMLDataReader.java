
import org.w3c.dom.*;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;


public class XMLDataReader {

    // This class is used to read the XML file and create the Bayesian network

    // This method reads the XML file and creates the Bayesian network
    public static void readXMLFile(String XMLFileName) {
        try{

            // Parse the XML file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("src/" + XMLFileName));
            doc.getDocumentElement().normalize();

            NodeList variableNodes = doc.getElementsByTagName("VARIABLE");

            for (int temp = 0; temp < variableNodes.getLength(); temp++) {
                Node parent = variableNodes.item(temp);
                if (parent.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element parentElement = (org.w3c.dom.Element) parent;
                    NodeList childNodes = parentElement.getChildNodes();

                    ArrayList<String> outcomes = new ArrayList<String>();

                    // Create a new Variable object
                    Variable variable = new Variable();

                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node childNode = childNodes.item(i);


                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            org.w3c.dom.Element childElement = (Element) childNode;

                            if ("NAME".equals(childElement.getTagName())) {
                                variable.setName(childElement.getTextContent());
                            }
                            if ("OUTCOME".equals(childElement.getTagName())) {
                                outcomes.add(childElement.getTextContent());
                            }

                        }
                        variable.setPossibleValues(outcomes.toArray(new String[outcomes.size()]));

                    }
                    BayesianNetworkManager.getInstance().addVariable(variable);
                }
            }


            NodeList definitions = doc.getElementsByTagName("DEFINITION");

            for (int temp = 0; temp < definitions.getLength(); temp++) {

                Node parent = definitions.item(temp);

                if (parent.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element parentElement = (org.w3c.dom.Element) parent;
                    NodeList childNodes = parentElement.getChildNodes();

                    ArrayList<String> givens = new ArrayList<String>();
                    String name = "";

                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node childNode = childNodes.item(i);


                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            org.w3c.dom.Element childElement = (Element) childNode;

                            if ("FOR".equals(childElement.getTagName())) {
                                name = childElement.getTextContent();
                            }
                            if ("GIVEN".equals(childElement.getTagName())) {
                                givens.add(childElement.getTextContent());
                            }

                            if ("TABLE".equals(childElement.getTagName())) {

                                // Split the table by the " " character and convert to double
                                String[] tableValues = childElement.getTextContent().split(" ");
                                double[] table = new double[tableValues.length];
                                for (int j = 0; j < tableValues.length; j++) {
                                    table[j] = Double.parseDouble(tableValues[j]);
                                }

                                // Set the probability table of the Variable object
                                BayesianNetworkManager.getInstance().getVariable(name).setProbabilityTable(table);

                            }

                        }

                        if(!givens.isEmpty()){
                            // Set the parents of the Variable object
                            Variable[] parents = new Variable[givens.size()];
                            for (int j = 0; j < givens.size(); j++) {
                                parents[j] = BayesianNetworkManager.getInstance().getVariable(givens.get(j));
                            }
                            BayesianNetworkManager.getInstance().getVariable(name).setParents(parents);

                        }
                    }
                }
            }

            // Set the children of the variables
            BayesianNetworkManager.getInstance().setChildren();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
