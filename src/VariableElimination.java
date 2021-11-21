import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class VariableElimination {

    private static Network net;

    public static void setNet(Network network) {
        net = network;
    }

    public static boolean BFS(String source, String destination, String[] evidences){

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

        while (!queue.isEmpty()){
            Variable var = queue.poll();
            for (Variable v: var.getParents()) {
                if (v.equals(dest)){ return true;}
                if (!visited.contains(v)){
                    visited.add(v);
                    queue.add(v);
                }
            }
        }
        return false;
    }
}
