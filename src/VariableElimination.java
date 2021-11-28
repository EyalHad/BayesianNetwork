import java.math.BigDecimal;
import java.util.*;

public class VariableElimination {
    private static double answer;
    private static int addition;
    private static int multiply;
    private static Network net;

    private String query = "";
    private String[] evidences;
    private final Queue<String> order = new LinkedList<>();

    private HashMap<Integer, Factor> factorMap = new HashMap<>();

    public VariableElimination(String q, String[] ev) {
        query = q;
        evidences = ev;
        addition = 0;
        multiply = 0;
    }


    public static void setNet(Network network) {
        net = network;
    }

    public boolean BFS(String source, String destination, String[] evidences) {

        Variable src = net.getVariable(source);
        Variable dest = net.getVariable(destination);

        HashSet<Variable> visited = new HashSet<>();
        Queue<Variable> queue = new LinkedList<>();

        visited.add(src);
        queue.add(src);

        if (evidences != null)
            for (String e : evidences) {
                // The evidence outcome is irrelevant for the algorithm, just need to know its there
                String[] leftSide = e.split("=");
                queue.add(net.getVariable(leftSide[0]));
            }

        while (!queue.isEmpty()) {
            Variable var = queue.poll();
            for (Variable v : var.getParents()) {
                if (v.equals(dest)) {
                    return true;
                }
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                }
            }
        }
        return false;
    }

    public boolean inCPT(String var, String[] evidences) {
        String[] over = var.split("=");
        Variable variable = net.getVariable(over[0]);
        if (variable.getParents().size() == 0) {
            if (evidences.length == 0) {
                answer = variable.getCpt().getCptMap().get(var);
                return true;
            } else {
                return false;
            }
        } else {
            Set<Variable> set = variable.getParents();
            HashSet<String> toQuery = new HashSet<>();
            toQuery.add(var);
            if (set.size() == evidences.length) {
                for (String ev :
                        evidences) {
                    String varString = ev.split("=")[0];
                    if (!set.contains(net.getVariable(varString))) {
                        return false;
                    }
                    toQuery.add(ev);
                }
                answer = variable.getCpt().getAsSets().get(toQuery);
                return true;
            } else {
                return false;
            }


        }
    }

    public static String getAnswer() {
        return String.format("%.5f", answer);
    }

    public static int getAddition() {
        return addition;
    }

    public static int getMultiply() {
        return multiply;
    }

    public void start() {

        while (order.size() != 1) {
            String hidden = order.poll();
            Queue<Factor> pq = new PriorityQueue<>(new FactorComparator());

            ArrayList<Integer> forRemoval = new ArrayList<>();
            for (int id : factorMap.keySet()) {

                Factor temp = factorMap.get(id);

                if (temp.names.contains(hidden)) {

                    pq.add(factorMap.get(id));
                    forRemoval.add(id);

                }

            }
            for (int name : forRemoval) {
                factorMap.remove(name);
            }

            while (pq.size() != 1 && !pq.isEmpty()) {
                System.out.println("-------------------PICK-------------------");
                Factor left = pq.poll();
                System.out.println(left.id+"\n"+left);
                System.out.println("-------------------PICK-------------------");
                Factor right = pq.poll();
                System.out.println(right.id+"\n"+right);
                Factor combined = new Factor(left,right);
                System.out.println("-------------------After JOIN-------------------");
                System.out.println(combined.id+"\n"+combined);
                pq.add(combined);
            }
            if (!pq.isEmpty()) {
                Factor afterSum = new Factor(pq.poll());
                System.out.println("-------------------After SUM-------------------");
                System.out.println(afterSum.id+"\n"+afterSum);
                this.factorMap.put(afterSum.id, afterSum);
            }

        }
        Queue<Factor> pq = new PriorityQueue<>(new FactorComparator());
        String q = query.split("=")[0];
        if (factorMap.size() > 1) {
            for (int id :
                    factorMap.keySet()) {
                Factor te = factorMap.get(id);
                if (te.names.contains(q))
                    pq.add(factorMap.get(id));
            }
        }
        while (pq.size() != 1) {
            Factor combined = new Factor(pq.poll(), pq.poll());
            pq.add(combined);
        }
        double bottom = 0;
        Factor last = pq.poll();
        for (double val :
                last.factorTable.values()) {
            addAddition();
            bottom += val;
        }
        addition--;
        List<String> que = new ArrayList<>();
        que.add(query);
        double up = last.factorTable.get(que);
        answer = up / bottom;


    }

    public void addToOrder(String hidden) {
        order.add(hidden);
    }

    public static void addMultiply() {
        multiply++;
    }

    public static void addAddition() {
        addition++;
    }

    public HashMap<Integer, Factor> getFactorMap() {
        return factorMap;
    }
}
