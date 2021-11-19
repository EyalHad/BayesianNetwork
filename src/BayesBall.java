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

    private final boolean independent;

    public BayesBall(String source, String destination, String[] evidences) {
        Variable src = net.getVariable(source);
        Variable dest = net.getVariable(destination);
        HashSet<Variable> evs = new HashSet<>();
        if (evidences != null)
            for (String e : evidences) {
                String[] leftSide = e.split("=");
                evs.add(net.getVariable(leftSide[0]));
            }
        independent = BouncingBall(null, src, dest, evs, Direction.UP);
    }

    public boolean BouncingBall(Variable from, Variable src, Variable dest, HashSet<Variable> evidence, Direction where) {
        if (src == dest) {return false;}


        else if (evidence.contains(src)) {
            if (where != Direction.UP) {
                for (Variable parent : src.getParents()) {
                    if (!BouncingBall(src, parent, dest, evidence, Direction.UP)) {return false;}
                }
            }
            return true;


        } else {
            if (where == Direction.DOWN) {
                for (Variable child : src.getChildren()) {
                    if (child != from) {
                        if (!BouncingBall(src, child, dest, evidence, Direction.DOWN)) {return false;}
                    }
                }
            } else {
                for (Variable parent : src.getParents()) {
                    if (parent != from) {
                        if (!BouncingBall(src, parent, dest, evidence, Direction.UP)) {return false;}
                    }
                }
                for (Variable child : src.getChildren()) {
                    if (child != from) {
                        if (!BouncingBall(src, child, dest, evidence, Direction.DOWN)) {return false;}
                    }
                }
            }
            return true;
        }

    }

    public boolean isIndependent() {
        return independent;
    }

}
