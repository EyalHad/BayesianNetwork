import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class Parser {


    public Parser(String FILENAME) {

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

            for (int i = 0; i < DEFINITION.getLength() ; i++) {

                Node var = DEFINITION.item(i);
                Element element = (Element) var;
                NodeList list = element.getChildNodes();

                String name = "";
                for (int j = 0; j < list.getLength(); j++) {
                    Node tmp = list.item(j);

                    try {
                        Element inner = (Element) tmp;
                        if (inner.getTagName().equals("FOR")) {
                            name = inner.getTextContent();
                        } else if (inner.getTagName().equals("GIVEN")) {
                            String given = inner.getTextContent();
                            Variable variable = net.getVariable(given);
                            net.addEdge(name,variable);
                        } else if (inner.getTagName().equals("TABLE")){
                            String table = inner.getTextContent();
                            String[] probabilities = table.split(" ");
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
