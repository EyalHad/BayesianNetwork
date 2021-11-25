
import java.util.*;
import java.util.stream.Collectors;

public class Factor {
    List<String> names;
    //    HashSet<Variable> variables = new HashSet<>();
    HashMap<List<String>, Double> factorTable;

    private static Network net;

    public static void setNet(Network network) {
        net = network;
    }

    public Factor(Variable variable, String[] evidences) {
        this.names = new ArrayList<>();
        this.factorTable = new HashMap<>();

        boolean flag = false;

        names.add(variable.getName());
        for (Variable parent :
                variable.getParents()) {
            names.add(parent.getName());
        }
        for (String name:
             names) {
            for (String ev:
                 evidences) {
                if (ev.split("=")[0].equals(name)) {
                    flag = true;
                    break;
                }
            }
        }
        HashMap<HashSet<String>, Double> asSets = variable.getCpt().getAsSets();
        List<String> key;

        if (!flag) {
            for (HashSet<String> s :
                    asSets.keySet()) {

                key = new ArrayList<String>(s);
                this.factorTable.put(key, asSets.get(s));
            }
        } else {
            for (HashSet<String> s :
                    asSets.keySet()) {

                key = new ArrayList<String>();

                Iterator<String> iterator = s.iterator();
                while (iterator.hasNext()) {
                    String toAdd = iterator.next();
                    for (String possibleParent :
                            evidences) {
                        if (toAdd.equals(possibleParent)) {
                            key.addAll(s);
                            this.factorTable.put(key, asSets.get(s));
                        }
                    }
                }
            }
        }

//        System.out.println(this);

    }

    public Factor(Factor first, Factor second, Variable var) {

        List<String> newNames = new ArrayList<>();
        for (String name1:
                first.names) {
            if (!newNames.contains(name1)) { newNames.add(name1); }
            for (String name2:
                 second.names) {
                if (!newNames.contains(name2)) { newNames.add(name2); }
            }
        }
        this.names = newNames;
        List<String> merge = first.names;
        merge.retainAll(second.names);
        merge = merge.stream().distinct().collect(Collectors.toList());

        for (String v: merge) {

        }

        this.factorTable = new HashMap<>();



        System.out.println(this);
    }


    private void changeToMatrix(Factor factor){
        int columns = factor.names.size() + 1;

    }
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(names.toString()).append("\n");
        for (List<String> key : factorTable.keySet()) {
            stringBuilder.append(key.toString()).append(" => ").append(factorTable.get(key)).append("\n");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}
