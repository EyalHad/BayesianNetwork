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

    private static String _XML_filename;
    private static final ArrayList<String> rawData = new ArrayList<>();

    private static String[] _Evidence;

    public InputHandler(String filename) {
        fileRead(filename);
    }

    public static void readXML() {
        new XMLParser(_XML_filename);
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
        int index = 1;
        String temp = rawData.get(index);

        while (temp.charAt(1) != '(') {

            String[] query = temp.split("\\|");
            if (query.length > 1) {
                _Evidence = query[1].split(",");
            }

            String[] leftSide = query[0].split("-");
            String src = leftSide[0];
            String dest = leftSide[1];

            BayesBall bounce = new BayesBall(src, dest, _Evidence);
            Output.addLine(bounce.isIndependent() ? "yes\n" : "no\n");

            temp = rawData.get(++index);
        }

        for (int i = index; i < rawData.size(); i++) {
            temp = rawData.get(i);
            temp = temp.substring(2);

            String[] query = temp.split("\\)");
            String[] hiddenALL = query[1].substring(1).split("-");
            String[] leftSide = query[0].split("\\|");

            String src = leftSide[0].charAt(0) + "";

            _Evidence = leftSide[1].split(",");

            Queue<String> toFactors = new LinkedList<>();

            for (String hidden : hiddenALL) {
                BayesBall bounce = new BayesBall(src, hidden, _Evidence);
                if (!bounce.isIndependent()) {
                    if (VariableElimination.BFS(src, hidden, _Evidence)) {
//                    System.out.println(rawData.get(i) + " checking: " + hidden + ", " + VariableElimination.BFS(src, hidden, _Evidence));
                        toFactors.add(hidden);
                    }
                }
            }
            Queue<Factor> factors = new LinkedList<>();
            while (!toFactors.isEmpty()) {
                Variable variable = XMLParser.net.getVariable(toFactors.poll());
                Factor factor = new Factor(variable, _Evidence, leftSide[0]);
                System.out.println(factor);
                factors.add(factor);
            }


        }
    }


    /**
     * Inner class to parser XML file
     */

    public static class XMLParser {

        public static Network net;

        public XMLParser(String FILENAME) {

            try {

                File inputFile = new File(FILENAME);

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputFile);

                doc.getDocumentElement().normalize();

                net = new Network();
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

                                    for (int l = 0; l < outcomes; l++) {
                                        StringBuilder set = new StringBuilder();
                                        tableNoParents[l + 1][0] = netVariable.getOutComes()[l];
                                        tableNoParents[l + 1][1] = probabilitiesAsStrings[l];
                                        set.append(varName + "=" + tableNoParents[l + 1][0]);
                                        netVariable.getCpt().addRow(set.toString(), Double.parseDouble(probabilitiesAsStrings[l]));

                                    }
                                    netVariable.getCpt().addMatrix(tableNoParents);

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
                                        StringBuilder set = new StringBuilder();

                                        String FOR = varName + "=" + cpt[line][0] + ", ";
                                        set.append(FOR);
                                        for (int k = 1; k < tableCOLUMNS - 1; k++) {
                                            String GIVENS = cpt[0][k] + "=" + cpt[line][k] + ", ";
                                            set.append(GIVENS);
                                        }

                                        double stringToDouble = Double.parseDouble(cpt[line][tableCOLUMNS - 1]);

                                        netVariable.getCpt().addRow(set.toString(), stringToDouble);
                                        netVariable.getCpt().addMatrix(cpt);
                                    }
                                }
                            }
                        } catch (ClassCastException ignored) { /* EMPTY */ }
                    }
                }
//            System.out.println(net.toString());
                BayesBall.setNet(net);
                VariableElimination.setNet(net);
                Factor.setNet(net);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}
