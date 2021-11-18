import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Input {

    private String _XML_filename;
    private final ArrayList<String> rawData;

    private String _StartNode;
    private String _GoalNode;
    private String[] _Evidence;


    public Input(String filename){
            rawData = new ArrayList<>();
            fileRead(filename);
            new XMLParser(_XML_filename);
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
            _XML_filename = rawData.get(0);
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void mineData(){
        int index = 1;
        String temp = rawData.get(index);

        while (temp.charAt(1) != '('){

            String[] query = temp.split("\\|");
            if (query.length > 1){ _Evidence = query[1].split(","); }

            String[] leftSide = query[0].split("-");
            _StartNode = leftSide[0];
            _GoalNode = leftSide[1];

            BayesBall bounce = new BayesBall(_StartNode,_GoalNode,_Evidence);
            Output.addLine(bounce.isIndependent() ? "yes\n" : "no\n");

            temp = rawData.get(++index);
        }
    }


}
