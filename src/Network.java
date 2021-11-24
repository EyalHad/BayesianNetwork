import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;


public class Network {

    private HashMap<String, HashSet<Variable>> graph;
    private HashMap<String, Variable> nodes;

    public Network() {
        graph = new HashMap<>();
        nodes = new HashMap<>();
    }

    public void addVariable(Variable variable) {
        HashSet<Variable> set = new HashSet<>();
        graph.put(variable.getName(), set);
        nodes.put(variable.getName(), variable);
    }

    public HashMap<String, Variable> getNodes() {
        return nodes;
    }

    public void addEdge(String name, Variable variable) {
        graph.get(name).add(variable);
    }

    public Variable getVariable(String name) {
        return nodes.get(name);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("~~~ Bayes Network: ~~~\n\n");
        for (Variable name : nodes.values()) {
            s.append(name.toString());
        }
        s.append("\n~~~ Graph: ~~~\n\n");
        for (String name : graph.keySet()) {
            HashSet<Variable> variables = graph.get(name);
            for (Variable var : variables) {
                s.append(var.getName())
                        .append("->").append(name).append(", ");
            }
            if (!variables.isEmpty()) {
                s.append("\n");
            }

        }
        return s.toString();
    }
}
