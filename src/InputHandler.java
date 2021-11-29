import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class InputHandler {

    private static String _XML_filename; // this will keep the XML file name
    private static final ArrayList<String> rawData = new ArrayList<>(); // every row of the input file, as it is,


    public InputHandler(String filename) {
        fileRead(filename);
    }

    public static void readXMLFile() {
        XMLParser.readXML(_XML_filename);
    }

    public static void startAlgorithms() {
        mineData();
    }

    private void fileRead(String input) {

        try {

            File file = new File(input);
            Scanner myReader = new Scanner(file);

            while (myReader.hasNextLine()) {

                String data = myReader.nextLine();
                rawData.add(data);

            }
            // first row of the file contains the xml file name
            _XML_filename = rawData.get(0);
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void mineData() {


        // The second row all the file contains the query's
        for (int i = 1; i < rawData.size(); i++) {
            String[] query = new String[0];
            String[] evidences = new String[0];
            String temp = rawData.get(i);
            String algorithm = (temp.charAt(1) != '(') ? "Bayes Ball" : "Variable Elimination";

            switch (algorithm) {

                case "Bayes Ball":

                    query = temp.split("\\|");
                    if (query.length > 1) {
                        evidences = query[1].split(",");
                    }

                    String[] leftSide = query[0].split("-");
                    String src = leftSide[0];
                    String dest = leftSide[1];

                    BayesBall bounce = new BayesBall(src, dest, evidences);
                    Output.addLine(bounce.isIndependent() ? "yes\n" : "no\n");
                    break;

                case "Variable Elimination":

                    temp = temp.substring(2);                                                             // "P(A=T|B=T,C=T) D-E-F" => "A=T|B=T,C=T) D-E-F"
                    String[] queryAndEvidencesAndHidden = temp.split("\\)");                         // "A=T|B=T,C=T) D-E-F" => ["A=T|B=T,C=T"], [" D-E-F"]
                    String[] allHidden = queryAndEvidencesAndHidden[1].substring(1).split("-");      // [" D-E-F"] =>  ["D"], ["E"], ["F"]
                    String[] queryAndEvidences = queryAndEvidencesAndHidden[0].split("\\|");         // ["A=T|B=T,C=T"] => ["A=T"], ["B=T,C=T"]
                    String queryVariable = queryAndEvidences[0].split("=")[0]; // src = "A"

                    String queryString = queryAndEvidences[0];
                    if (queryAndEvidences.length > 1)
                        evidences = queryAndEvidences[1].split(",");                                     // ["B=T,C=T"] => ["B=T"], ["C=T"]

                    VariableElimination variableElimination = new VariableElimination(queryString, evidences);
                    System.out.println("####################################################################################################");
                    System.out.println(rawData.get(i));
                    if (variableElimination.inCPT(queryString, evidences)) {
                        Output.addLine(VariableElimination.getAnswer() + "," +
                                VariableElimination.getAddition() + "," +
                                VariableElimination.getMultiply() + "\n");
                        continue;
                    }
                    int id = 0;
                    for (String name : XMLParser.net.getNodes().keySet()) {
                        Variable variable = XMLParser.net.getVariable(name);
                        Factor factor = new Factor(variable, evidences, ++id);
                        variableElimination.getFactorMap().put(factor.id, factor);
                    }
                    System.out.println("####################################################################################################");


                    for (String hidden : allHidden) {

                        BayesBall bayesBall = new BayesBall(queryVariable, hidden, evidences);
                        if (!bayesBall.isIndependent()) {

                            if (variableElimination.BFS(queryVariable, hidden, evidences)) {
                                variableElimination.addToOrder(hidden);
                            } else {
                                List<Integer> integerList = new ArrayList<>();
                                for (int rm: variableElimination.getFactorMap().keySet()) {
                                    Factor factor = variableElimination.getFactorMap().get(rm);
                                    if (factor.names.contains(hidden)){
                                        integerList.add(rm);
                                    }
                                }
                                for (int rm:
                                     integerList) {
                                    variableElimination.getFactorMap().remove(rm);
                                }
                            }
                        } else {
                            List<Integer> integerList = new ArrayList<>();
                            for (int rm: variableElimination.getFactorMap().keySet()) {
                                Factor factor = variableElimination.getFactorMap().get(rm);
                                if (factor.names.contains(hidden)){
                                    integerList.add(rm);
                                }
                            }
                            for (int rm:
                                    integerList) {
                                variableElimination.getFactorMap().remove(rm);
                            }

                        }
                    }
                    variableElimination.addToOrder(queryVariable);
                    variableElimination.start();
                    Output.addLine(VariableElimination.getAnswer() + "," +
                            VariableElimination.getAddition() + "," +
                            VariableElimination.getMultiply() + "\n");

            }

        }

    }


    /**
     * Inner class to parser XML file
     */

    public static class XMLParser {

        public static Network net;
        private static boolean print = true;

        public static void readXML(String FILENAME) {

            try {

                File inputFile = new File(FILENAME);

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputFile);

                doc.getDocumentElement().normalize();

                /** Initializing Network */
                net = new Network();

                /** Contain each VARIABLE as a LIST */
                NodeList VARIABLE = doc.getElementsByTagName("VARIABLE");

                for (int i = 0; i < VARIABLE.getLength(); i++) {

                    Node var = VARIABLE.item(i);
                    Element element = (Element) var;
                    NodeList list = element.getChildNodes();

                    String varName = "";
                    StringBuilder outcomesBuilder = new StringBuilder();
                    for (int j = 0; j < list.getLength(); j++) {
                        Node tmp = list.item(j);

                        try {
                            Element inner = (Element) tmp;
                            if (inner.getTagName().equals("NAME")) {
                                varName = tmp.getTextContent();

                            } else if (inner.getTagName().equals("OUTCOME")) {
                                outcomesBuilder.append(tmp.getTextContent()).append(" ");

                            }
                        } catch (Exception ignored) { /* EMPTY */ }
                    }
                    Variable v = new Variable(varName, outcomesBuilder.toString().split(" "));
                    net.addVariable(v);
                }

                NodeList DEFINITION = doc.getElementsByTagName("DEFINITION");

                for (int i = 0; i < DEFINITION.getLength(); i++) {

                    Node var = DEFINITION.item(i);
                    Element element = (Element) var;
                    NodeList list = element.getChildNodes();

                    String varName = "";
                    StringBuilder givens = new StringBuilder();
                    for (int j = 0; j < list.getLength(); j++) {
                        Node tmp = list.item(j);

                        try {
                            Element inner = (Element) tmp;
                            if (inner.getTagName().equals("FOR")) {

                                varName = inner.getTextContent();

                            } else if (inner.getTagName().equals("GIVEN")) {

                                String given = inner.getTextContent();
                                givens.append(given).append(" ");

                                Variable variable = net.getVariable(given);
                                net.addEdge(varName, variable);
                                net.getVariable(varName).addParent(variable);
                                net.getVariable(variable.getName()).addChild(net.getVariable(varName));

                            } else if (inner.getTagName().equals("TABLE")) {

                                String table = inner.getTextContent();
                                String[] probabilitiesAsStrings = table.split(" ");
                                String[] parents = givens.toString().split(" ");

                                Variable netVariable = net.getVariable(varName);
                                int outcomes = netVariable.getOutComes().length;

                                if (parents[0].equals("")) {

                                    String[][] tableNoParents = new String[outcomes + 1][2];
                                    tableNoParents[0][0] = varName;
                                    tableNoParents[0][1] = "Probability";

                                    for (int l = 0; l < outcomes; l++) {
                                        StringBuilder set = new StringBuilder();
                                        HashSet<String> vars = new HashSet<>();

                                        tableNoParents[l + 1][0] = netVariable.getOutComes()[l];
                                        tableNoParents[l + 1][1] = probabilitiesAsStrings[l];

                                        vars.add(varName + "=" + tableNoParents[l + 1][0]);
                                        set.append(varName).append("=").append(tableNoParents[l + 1][0]);
                                        netVariable.getCpt().addRow(set.toString(), Double.parseDouble(probabilitiesAsStrings[l]));
                                        netVariable.getCpt().addDependency(vars, Double.parseDouble(probabilitiesAsStrings[l]));


                                    }
                                    netVariable.getCpt().addMatrix(tableNoParents);
                                    if (print) {
                                        for (String[] s : tableNoParents) {
                                            System.out.println(Arrays.toString(s));
                                        }
                                        System.out.println("--------------------------------------");
                                    }

                                } else {

                                    int probabilities = probabilitiesAsStrings.length;
                                    int tableROWS = probabilities + 1;
                                    int tableCOLUMNS = parents.length + 2;
                                    String[][] cpt = new String[tableROWS][tableCOLUMNS];

                                    // Adding the varName and it`s parents to the first ROW of the cpt
                                    cpt[0][0] = varName;
                                    for (int k = 1; k <= parents.length; k++) {
                                        cpt[0][k] = parents[parents.length - k];
                                    }
                                    cpt[0][parents.length + 1] = "Probability"; // otherwise, will be null spot

                                    // initiate the varName row with all it`s outcomes
                                    int row = 1;
                                    while (row < probabilities) {
                                        for (String outcome : netVariable.getOutComes()) {
                                            cpt[row][0] = outcome;
                                            row++;
                                        }
                                    }

                                    // Adding the parents outcomes to the table with the given order
                                    int u = 1;
                                    int tempOutcome = netVariable.getOutComes().length;
                                    for (int x = parents.length - 1; x >= 0; x--) {
                                        Variable parent = net.getVariable(parents[x]);
                                        int loop = tempOutcome;
                                        row = 1;
                                        while (row < probabilities) // the length of a column
                                            for (String outcome : parent.getOutComes()) {
                                                int noName = 0;
                                                while (noName < loop /* && row <= probabilities */) {
                                                    cpt[row][u] = outcome;
                                                    row++;
                                                    noName++;
                                                }
                                            }
                                        tempOutcome *= parent.getOutComes().length;
                                        u++;
                                    }


                                    u = parents.length + 1;
                                    row = 1;
                                    while (row < probabilities) {
                                        for (String probability : probabilitiesAsStrings) {
                                            cpt[row][u] = probability;
                                            row++;
                                        }
                                    }

                                    // Adding the information that has been gathered to the network
                                    for (int line = 1; line < tableROWS; line++) {
                                        HashSet<String> vars = new HashSet<>();
                                        StringBuilder set = new StringBuilder();

                                        String FOR = varName + "=" + cpt[line][0] + ", ";
                                        set.append(FOR);
                                        vars.add(FOR.substring(0, FOR.length() - 2));
                                        for (int k = 1; k < tableCOLUMNS - 1; k++) {
                                            String GIVENS = cpt[0][k] + "=" + cpt[line][k] + ", ";
                                            set.append(GIVENS);
                                            vars.add(GIVENS.substring(0, GIVENS.length() - 2));
                                        }

                                        double stringToDouble = Double.parseDouble(cpt[line][tableCOLUMNS - 1]);
                                        netVariable.getCpt().addDependency(vars, stringToDouble);
                                        netVariable.getCpt().addRow(set.toString(), stringToDouble);
                                        netVariable.getCpt().addMatrix(cpt);


                                    }

                                    if (print) {
                                        for (String[] s : cpt) {
                                            System.out.println(Arrays.toString(s));
                                        }
                                        System.out.println("--------------------------------------");
                                    }

                                }
                            }
                        } catch (ClassCastException ignored) { /* EMPTY */ }
                    }
                }
//                System.out.println(net.toString());
                BayesBall.setNet(net);
                VariableElimination.setNet(net);
                Factor.setNet(net);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}
