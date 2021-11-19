import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;

public class XMLParser {

    public Network net ;

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
                                HashSet<String> set = new HashSet<>();
                                for (int l = 0; l < outcomes; l++) {

                                    tableNoParents[l + 1][0] = netVariable.getOutComes()[l];
                                    tableNoParents[l + 1][1] = probabilitiesAsStrings[l];
                                    set.add(varName + "=" + tableNoParents[l + 1][0]);
                                    netVariable.getCpt().addRow(set, Double.parseDouble(probabilitiesAsStrings[l]));

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
                                    HashSet<String> set = new HashSet<>();

                                    String FOR = varName + "=" + cpt[line][0];
                                    set.add(FOR);
                                    for (int k = 1; k < tableCOLUMNS - 1; k++) {
                                        String GIVENS = cpt[0][k] + "=" + cpt[line][k];
                                        set.add(GIVENS);
                                    }

                                    double stringToDouble = Double.parseDouble(cpt[line][tableCOLUMNS - 1]);

                                    netVariable.getCpt().addRow(set, stringToDouble);
                                    netVariable.getCpt().addMatrix(cpt);
                                }
                            }
                        }
                    } catch (ClassCastException ignored) { /* EMPTY */ }
                }
            }
            System.out.println(net.toString());
            BayesBall.setNet(net);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
