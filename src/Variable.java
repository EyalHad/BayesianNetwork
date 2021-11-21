import java.util.*;
import java.util.stream.Collectors;

public class Variable {


    private final String name;
    private final String[] outComes;
    private final Set<Variable> parents = new HashSet<>();
    private final Set<Variable> children = new HashSet<>();

    private CPT cpt = new CPT(); // Conditional Probability Table


    public Variable(String n, String[] o) {
        outComes = o;
        name = n;
    }

    public CPT getCpt() {
        return cpt;
    }

    public void addParent(Variable variable) {
        parents.add(variable);
    }

    public void addChild(Variable variable) {
        children.add(variable);
    }

    public String getName() {
        return name;
    }

    public String[] getOutComes() {
        return outComes;
    }

    public Set<Variable> getParents() {
        return parents;
    }

    public Set<Variable> getChildren() {
        return children;
    }


    @Override
    public String toString() {
        return "Variable: " + name + "\n" + printParents() + "\n" + printChildren() +
                "\nOutcomes = " + Arrays.toString(outComes) + "\n" + cpt + "\n";
    }

    private String printParents() {
        if (parents.isEmpty()) return "NO PARENTS";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Parents: ");
        for (Variable v : parents) {
            stringBuilder.append(v.getName()).append(", ");
        }
        return stringBuilder.toString();
    }

    private String printChildren() {
        if (children.isEmpty()) return "NO CHILDREN";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Children: ");
        for (Variable v : children) {
            stringBuilder.append(v.getName()).append(", ");
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name) &&
                Objects.equals(parents, variable.parents) &&
                Objects.equals(children, variable.children);
    }


    public static class CPT {

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

        public HashMap<String, Double> get_TABLE() {
            return _TABLE;
        }

        @Override
        public String toString() {
            return _TABLE.keySet().stream()
                    .map(s -> s + "-> " + _TABLE.get(s) + "\n")
                    .collect(Collectors.joining());
        }

    }


}
