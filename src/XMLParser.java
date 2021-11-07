import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;

public class XMLParser {


    public XMLParser(String FILENAME) {

        try {

            File inputFile = new File(FILENAME);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);

            doc.getDocumentElement().normalize();

            Network net = new Network();
            NodeList VARIABLE = doc.getElementsByTagName("VARIABLE");

            for (int i = 0; i < VARIABLE.getLength(); i++) {

                Node var = VARIABLE.item(i);
                Element element = (Element) var;
                NodeList list = element.getChildNodes();

                String name = "";
                StringBuilder outcomes = new StringBuilder();
                for (int j = 0; j < list.getLength(); j++) {
                    Node tmp = list.item(j);

                    try {
                        Element inner = (Element) tmp;
                        if (inner.getTagName().equals("NAME")) {
                            name = tmp.getTextContent();

                        } else if (inner.getTagName().equals("OUTCOME")) {
                            outcomes.append(tmp.getTextContent()).append(" ");

                        }
                    } catch (Exception ignored) { /* EMPTY */ }
                }
                Variable v = new Variable(name, outcomes.toString().split(" "));
                net.addVariable(v);
            }

            NodeList DEFINITION = doc.getElementsByTagName("DEFINITION");

            for (int i = 0; i < DEFINITION.getLength(); i++) {

                Node var = DEFINITION.item(i);
                Element element = (Element) var;
                NodeList list = element.getChildNodes();

                String name = "";
                StringBuilder givens = new StringBuilder();
                for (int j = 0; j < list.getLength(); j++) {
                    Node tmp = list.item(j);

                    try {
                        Element inner = (Element) tmp;
                        if (inner.getTagName().equals("FOR")) {

                            name = inner.getTextContent();

                        } else if (inner.getTagName().equals("GIVEN")) {

                            String given = inner.getTextContent();
                            givens.append(given).append(" ");

                            Variable variable = net.getVariable(given);
                            net.addEdge(name, variable);
                            net.getVariable(name).addParent(variable);

                        } else if (inner.getTagName().equals("TABLE")) {


                            String table = inner.getTextContent();
                            String[] probabilities = table.split(" ");

                            String[] parents = givens.toString().split(" ");
                            Variable[] vars = new Variable[parents.length];


                            for (int k = 0; k < parents.length; k++) {
                                vars[k] = net.getVariable(parents[parents.length - 1 - k]);
                            }


                            int outcomes = net.getVariable(name).getOutComes().length;
                            Variable v = net.getVariable(name);


                            if (vars[0] != null) { // No Parents

                                for (int k = 0; k < probabilities.length; k = k + outcomes) {

                                    for (int l = k; l < k + outcomes; l++) {
                                        String out = net.getVariable(name).getOutComes()[l % outcomes];
                                        String[] result = new String[vars.length + 1];
                                        result[0] = name + "=" + v.getOutComes()[l % outcomes];

                                        v.getCpt().addRow(result, Double.parseDouble(probabilities[l]));
                                    }
                                }
                            } else {

                                for (int l = 0; l < outcomes; l++) {
                                    String[] result = new String[1];
                                    result[0] = name + "=" + v.getOutComes()[l];
                                    v.getCpt().addRow(result, Double.parseDouble(probabilities[l]));
                                }
                            }
                        }
                    } catch (Exception ignored) { /* EMPTY */ }
                }
            }

            System.out.println(net.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
