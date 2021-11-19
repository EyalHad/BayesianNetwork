import java.util.*;

public class Variable {


    private final String name;
    private final String[] outComes;
    private Set<Variable> parents = new HashSet<>();
    private Set<Variable> children = new HashSet<>();

    private CPT cpt = new CPT(); // Conditional Probability Table


    public Variable(String n, String[] o){
        outComes = o;
        name = n;
    }

    public CPT getCpt() {
        return cpt;
    }

    public void addParent(Variable variable){
        parents.add(variable);
    }
    public void addChild(Variable variable){
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
    public Set<Variable> getChildren() { return children;}


    @Override
    public String toString() {
        return "Variable: " + name + "\n" + printParents() + "\n" + printChildren() +
                "\nOutcomes = " + Arrays.toString(outComes) +"\n"+cpt +"\n";
    }

    private String printParents(){
        if (parents == null) return "NO PARENTS";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Parents: ");
        for (Variable v: parents) {
            stringBuilder.append(v.getName()).append(", ");
        }
        return stringBuilder.toString();
    }

    private String printChildren(){
        if (children == null) return "NO CHILDREN";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Children: ");
        for (Variable v: children) {
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


}
