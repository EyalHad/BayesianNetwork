import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.Arrays;

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

                            int outcomes = net.getVariable(name).getOutComes().length;
                            Variable v = net.getVariable(name);
                            if (parents[0].equals("")) {
                                for (int l = 0; l < outcomes; l++) {
                                    String[] result = new String[1];
                                    result[0] = name + "=" + v.getOutComes()[l];
                                    v.getCpt().addRow(result, Double.parseDouble(probabilities[l]));
                                }

                            } else {
                                String[][] cpt = new String[probabilities.length + 1][parents.length + 2];
                                cpt[0][0] = name;
                                for (int k = 1; k <= parents.length; k++) {
                                    cpt[0][k] = parents[parents.length - k];
                                }
                                cpt[0][parents.length + 1] = "P";


                                int w = 1;
                                while (w < probabilities.length)
                                    for (String outcome : v.getOutComes()) {
                                        cpt[w][0] = outcome;
                                        w++;
                                    }


                                int u = 1;
                                int tempOutcome = net.getVariable(name).getOutComes().length;
                                for (int x = parents.length -1; x >= 0; x--) {
                                    Variable variable = net.getVariable(parents[x]);
                                    int loop = tempOutcome;
                                    w = 1;
                                    while (w < probabilities.length)
                                        for (String outcome : variable.getOutComes()) {
                                            int noName = 0;
                                            while (noName < loop && w <= probabilities.length) {
                                                cpt[w][u] = outcome;
                                                w++;
                                                noName++;
                                            }
                                        }
                                    tempOutcome *= variable.getOutComes().length;
                                    u++;
                                }
                                u = parents.length+1;
                                w = 1;
                                while (w < probabilities.length)
                                    for (String probability : probabilities) {
                                        cpt[w][u] = probability;
                                        w++;
                                    }
                                for (int line = 1; line < cpt.length; line++){
                                    String FOR = cpt[0][0] + "=" + cpt[line][0] + "|";
                                    StringBuilder GIVENS = new StringBuilder();
                                    for (int k = 1; k < cpt[0].length-1; k++) {
                                        GIVENS.append(cpt[0][k]).append("=").append(cpt[line][k]);
                                        if (k != cpt[0].length-2){
                                            GIVENS.append(",");
                                        }
                                    }
//                                    System.out.println(cpt[line][cpt[0].length-1]);
                                    double stringToDouble = Double.parseDouble(cpt[line][cpt[0].length-1]);
                                    String[] strings = {FOR+GIVENS.toString()};
//                                    System.out.println(FOR);
//                                    System.out.println(GIVENS);
                                    v.getCpt().addRow(strings,stringToDouble);
                                }



                            }

                        }
                    } catch (ClassCastException ignored) { /* EMPTY */ }
                }
            }

            System.out.println(net.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
