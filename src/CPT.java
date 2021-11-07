import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CPT {

    private HashMap<String[],Double> _TABLE;

    public CPT(){
        _TABLE = new HashMap<>();
    }

    public void addRow(String[] givens,double probability){
        _TABLE.put(givens,probability);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String[] s: _TABLE.keySet()) {
            stringBuilder.append(Arrays.toString(s)).append(" ").append(_TABLE.get(s)).append(", ");
        }
        return "CPT:\n"+ stringBuilder.toString()+"\n";
    }

}
