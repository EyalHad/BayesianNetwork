import java.math.BigDecimal;
import java.util.*;

public class VariableElimination {
    private static double answer;
    private static Network net;

    private String query = "";
    private String[] evidences;
    private final Queue<String> order = new LinkedList<>();

    private HashMap<List<String>, Factor> factorMap = new HashMap<>();

    public VariableElimination(String q, String[] ev) {
        query = q;
        evidences = ev;
    }

    private static Factor minWhile;

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

//    public boolean inCPT(String var, String[] evidences) {
//        String[] tovar = var.split("=");
//        Variable child = net.getVariable(tovar[0]);
//        int parents = child.getParents().size();
//        int checksum = 0;
//        HashSet<String> q = new HashSet<>();
//        for (String parentAstring : evidences) {
//            String[] split = parentAstring.split("=");
//            Variable parent = net.getVariable(split[0]);
//            if (child.getParents().contains(parent)) {
//                q.add(parentAstring);
//                checksum++;
//            }
//        }
//        if (checksum != parents) {
//            return false;
//        } else {
//            q.add(var);
//            answer = child.getCpt().getForQuery().get(q);
//            System.out.println(getAnswer());
//        }
//        return true;
//    }



    public static String getAnswer() {
        return String.format("%.5f", answer);
    }

    public void start() {

        while (!order.isEmpty()){

            String hidden = order.poll();
            Queue<Factor> pq = new PriorityQueue<>(new FactorComparator());

            ArrayList<List<String>> forRemoval = new ArrayList<>();
            for (List<String> name:
                 factorMap.keySet()) {
                if (name.contains(hidden)){

                    pq.add(factorMap.get(name));
                    forRemoval.add(name);
                }
            }
            for (List<String> name: forRemoval) {
                factorMap.remove(name);
            }

            while (pq.size() != 1){
                Factor combined = new Factor(pq.poll(),pq.poll(), net.getVariable(hidden));
                pq.add(combined);
            }
        }

    }

    public void addToOrder(String hidden) {
        order.add(hidden);
    }

    public HashMap<List<String>, Factor> getFactorMap() {
        return factorMap;
    }
}
