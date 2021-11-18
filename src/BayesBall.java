import java.util.HashSet;

public class BayesBall {

    enum Direction {
        UP,
        DOWN
    }

    private static Network net;

    public static void setNet(Network network) {
        net = network;
    }

    private boolean independent;

    public BayesBall(String source, String destination, String[] evidences) {
        net.getVariable(source);
        net.getVariable(destination);
        HashSet<Variable> evs = new HashSet<>();
        if (evidences != null)
            for (String e : evidences) {
                String[] leftSide = e.split("=");
                net.getVariable(leftSide[0]);
            }
        System.out.println("asdasd");
    }

    public BayesBall(Variable from, Variable src, Variable dest, HashSet<Variable> evidence, Direction where) {

    }

    public boolean isIndependent() {
        return independent;
    }

}
