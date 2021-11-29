
import java.util.*;
import java.util.stream.Collectors;

public class Factor {
    public int id;
    List<String> names;
    //    HashSet<Variable> variables = new HashSet<>();
    HashMap<List<String>, Double> factorTable;
    private static List<String> evidence;
    private static int nums;

    private static Network net;

    public static void setNet(Network network) {
        net = network;
    }

    public Factor(Variable variable, String[] evidences, int id) {
        if (id == 0) {
            Factor.nums = 0;
        } else {
            Factor.nums++;
        }
        this.id = id;

        this.evidence = new ArrayList<>();
        this.names = new ArrayList<>();
        this.factorTable = new HashMap<>();

        boolean flag = false;

        names.add(variable.getName());
        for (Variable parent :
                variable.getParents()) {
            names.add(parent.getName());
        }
        List<String> toRrR = new ArrayList<>();
        for (String name : names) {
            for (String ev : evidences) {
                evidence.add(ev);
                if (ev.split("=")[0].equals(name)) {
                    flag = true;
                    toRrR.add(name);
                    break;
                }
            }
        }
        names.removeAll(toRrR);
        HashMap<HashSet<String>, Double> asSets = variable.getCpt().getAsSets();
        List<String> key;

        if (!flag) {
            for (HashSet<String> s :
                    asSets.keySet()) {

                key = new ArrayList<String>(s);
                this.factorTable.put(key, asSets.get(s));
            }
        } else {

            List<String> evidenceAsList = new ArrayList<>();

            for (String s : evidences) {
                String spliy = s.split("=")[0];
                if (variable.getParents().contains(net.getVariable(spliy)))
                    evidenceAsList.add(s);
            }
            for (HashSet<String> s : asSets.keySet()) {

                List<String> mashu = new ArrayList<>(s);
                mashu.retainAll(evidenceAsList);
                mashu = mashu.stream().distinct().collect(Collectors.toList());
                List<String> rmoving = new ArrayList<>();
                for (String string:
                     s) {
                    for (String evi:
                         evidences) {
                        if(string.split("=")[0].equals(evi.split("=")[0])){
                            rmoving.add(string);
                        }
                    }
                }
                HashSet<String> copy = new HashSet<>(s);
                for (String more:
                    rmoving ) {
                    if( s.contains(more)){
                        copy.remove(more);
                    }
                }

                if (mashu.size() == evidenceAsList.size() ) {

                    key = new ArrayList<String>();

                    key.addAll(copy);
                    this.factorTable.put(key, asSets.get(s));
                }
            }
        }
//        System.out.println(this);
    }

    public Factor(Factor first, Factor second) {

        Factor.nums++;
        this.id = Factor.nums;
//        List<String> newNames = new ArrayList<>();
        List<String> inCommon = new ArrayList<>();
        List<String> unCommon = new ArrayList<>();

        for (String name1 : first.names) {
            if (second.names.contains(name1)) {
                inCommon.add(name1);
            } else {
                unCommon.add(name1);
            }
        }
        for (String name2 : second.names) {
            if (first.names.contains(name2)) {
                inCommon.add(name2);
            } else {
                unCommon.add(name2);
            }
        }

        inCommon = inCommon.stream().distinct().collect(Collectors.toList());
        unCommon = unCommon.stream().distinct().collect(Collectors.toList());
        this.names = new ArrayList<>(inCommon);
//        this.names.addAll(unCommon);
        this.factorTable = new HashMap<>();
        for (List<String> rowONE : first.factorTable.keySet()) {
            for (List<String> rowTWO : second.factorTable.keySet()) {
                List<String> both = new ArrayList<>(rowONE);
                both.retainAll(rowTWO);
                if (both.size() == inCommon.size()) {
                    List<String> key = new ArrayList<>(rowONE);
                    key.addAll(rowTWO);
                    key = key.stream().distinct().collect(Collectors.toList());
                    List<String> forRemoving = new ArrayList<>();
//                    for (String toRemove : key) {
//                        if (evidence.contains(toRemove)) {
//                            forRemoving.add(toRemove);
//                        }
//                    }
//                    for (String remove :
//                            forRemoving) {
//                        key.remove(remove);
//                    }
                    VariableElimination.addMultiply();
                    double value = first.factorTable.get(rowONE) * second.factorTable.get(rowTWO);
                    this.factorTable.put(key, value);
                }
            }
        }


//        System.out.println(this);
    }

    public Factor(Factor toSum) {
        this.id = Factor.nums;
        Factor.nums++;
        this.factorTable = new HashMap<>();
        this.names = new ArrayList<>();
        HashSet<List<String>> toAvoid = new HashSet<>();
        for (List<String> name : toSum.factorTable.keySet()) {
            for (String s : name) {
                String[] temp = s.split("=");
                if (!toSum.names.contains(temp[0])) {
                    this.names.add(temp[0]);
                }
            }
        }
        this.names = this.names.stream().distinct().collect(Collectors.toList());
        for (List<String> row1 : toSum.factorTable.keySet()) {
            List<String> subtract1 = new ArrayList<>(row1);
            for (String s : row1) {
                String[] temp = s.split("=");
                if (toSum.names.contains(temp[0])) {
                    subtract1.remove(s);
                }
            }
            for (List<String> row2 : toSum.factorTable.keySet()) {
                if (row1.equals(row2)) continue;

                List<String> subtract2 = new ArrayList<>(row2);
                for (String s : row2) {
                    String[] temp = s.split("=");
                    if (toSum.names.contains(temp[0])) {
                        subtract2.remove(s);
                    }
                }
                List<String> both = new ArrayList<>(subtract1);
                both.retainAll(subtract2);
                if (both.size() == subtract1.size())
                    if (!toAvoid.contains(both)) {
                        toAvoid.add(both);
                        double value = toSum.factorTable.get(row1) + toSum.factorTable.get(row2);
                        VariableElimination.addAddition();
                        this.factorTable.put(both, value);
                    }

            }
        }
//        System.out.println(this);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(names.toString()).append("\n");
        for (List<String> key : factorTable.keySet()) {
            String temp = String.format("%.10f", factorTable.get(key));
            stringBuilder.append(key.toString()).append(" => ").append(temp).append("\n");
        }
        stringBuilder.append("\n");
        String toPrint = stringBuilder.toString();
        return toPrint;
    }
}
