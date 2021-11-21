import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Input {

    private static String _XML_filename;
    private static final ArrayList<String> rawData = new ArrayList<>();

    private static String[] _Evidence;


    public Input(String filename) {
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

            for (String hidden: hiddenALL) {
                BayesBall bounce = new BayesBall(src, hidden, _Evidence);
                if (!bounce.isIndependent()){
                    if(VariableElimination.BFS(src, hidden, _Evidence)) {
                        toFactors.add(hidden);
                    }
                }
            }




        }
    }
}
