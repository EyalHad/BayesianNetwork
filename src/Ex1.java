
public class Ex1 {

    private static final String FILENAME = "input.txt";

    public static void main(String[] args) {

        new InputHandler(FILENAME);
        InputHandler.readXMLFile();
        InputHandler.startAlgorithms();

        // The outcome from each algorithm is written to output.txt
        Output.writeToFile();
    }

}
