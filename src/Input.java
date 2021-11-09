import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Input {

    private String _XML_filename;
    private ArrayList<String> rawData = new ArrayList<>();


    public Input(String filename){
            fileRead(filename);
            new XMLParser(_XML_filename);

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
}
