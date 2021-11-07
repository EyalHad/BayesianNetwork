import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Variable {


    private final String name;
    private final String[] outComes;
    private Set<Variable> parents;

    private CPT cpt = new CPT(); // Conditional Probability Table


    public Variable(String n, String[] o){
        outComes = o;
        name = n;
    }

    public CPT getCpt() {
        return cpt;
    }

    void addParent(Variable variable){
        if (parents == null){ parents = new HashSet<>(); }
        parents.add(variable);
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


    @Override
    public String toString() {
        return "Variable: " + name + "\n" +printParents() +
                "\nOutcomes = " + Arrays.toString(outComes) +"\n"+cpt;
    }

    private String printParents(){
        if (parents == null) return  "";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Parents: ");
        for (Variable v: parents) {
            stringBuilder.append(v.getName()).append(", ");
        }
        return stringBuilder.toString();
    }
}
