import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Output {

    private static ArrayList<String> lines;
    private static final String FILENAME = "output.txt";
    public static void setLines() {
        lines = new ArrayList<>();
    }

    public static void addLine(String line) {
        if (lines == null) {
            setLines();
        }
        lines.add(line);
    }

    public static void writeToFile() {
        try {
            FileWriter myWriter = new FileWriter(FILENAME);
            for (String toWrite :
                    lines) {
                myWriter.write(toWrite);
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
