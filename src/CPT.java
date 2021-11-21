import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CPT {

    private final HashMap<String, Double> _TABLE;
    private String[][] matrix;

    public CPT() {
        _TABLE = new HashMap<>();
    }

    public void addRow(String givens, double probability) {
        _TABLE.put(givens, probability);
    }

    public void addMatrix(String[][] matrix) {
        this.matrix = matrix;
    }

    public String[][] getMatrix() {
        return matrix;
    }

    @Override
    public String toString() {
        return _TABLE.keySet().stream()
                .map(s -> s.toString() + "->" + _TABLE.get(s) + "\n")
                .collect(Collectors.joining());
    }




}
