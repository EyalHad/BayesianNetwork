import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Variable {


    private String name;
    private String[] outComes;
    private Set<Variable> parents;


    public Variable(String n, String[] o){
        outComes = o;
        name = n;
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
        return "Variable: " + name +
                "\nOutcomes = " + Arrays.toString(outComes) +"\n";
    }
}
