import java.util.ArrayList;
import java.util.HashMap;

public class Factor {
    Variable variable;
    HashMap<String, Double> _FACTOR_TABLE = new HashMap<>();

    private static Network net;
    public static void setNet(Network network) {
        net = network;
    }

    public Factor(Variable variable, String[] evidences, String q){
//        System.out.println(variable.getName());
        this.variable = variable;
        HashMap<String,Double> cptTable = variable.getCpt().get_TABLE();
        ArrayList<String> parentInEvidence = new ArrayList<>();
        parentInEvidence.add(q);
        for (String ev:evidences) {
            String[] parent = ev.split("=");
            Variable potentialParent = net.getVariable(parent[0]);
            if (variable.getParents().contains(potentialParent)){
                parentInEvidence.add(ev);
            }
        }
        for (String key: cptTable.keySet()) {
            for (int i = 0; i < parentInEvidence.size(); i++) {
                if (key.contains(parentInEvidence.get(i))){
                    _FACTOR_TABLE.put(key,cptTable.get(key));
                }
            }
        }
        if (_FACTOR_TABLE.isEmpty()){
            for (String key: cptTable.keySet()) {
                _FACTOR_TABLE.put(key,cptTable.get(key));
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(variable.getName()).append("\n");
        for (String key: _FACTOR_TABLE.keySet()) {
            stringBuilder.append(key).append(" => ").append(_FACTOR_TABLE.get(key)).append("\n");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
