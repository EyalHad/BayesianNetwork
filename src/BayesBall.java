import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class BayesBall {

    enum Direction {
        UP,
        DOWN
    }

    private HashMap<Variable, Boolean> fromChild = new HashMap<>();
    private HashMap<Variable, Boolean> fromParent = new HashMap<>();

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
                // The evidence outcome is irrelevant for the algorithm, just need to know its there
                String[] leftSide = e.split("=");
                evs.add(net.getVariable(leftSide[0]));
            }
        for (String v : net.getNodes().keySet()) {
            fromChild.put(net.getVariable(v), false);
            fromParent.put(net.getVariable(v), false);
        }
        // When starting the algorithm - the node that we're coming from is not exist therefore null
        independent = BouncingBall(null, src, dest, evs, Direction.UP);
    }

    public boolean BouncingBall(Variable from, Variable src, Variable dest, HashSet<Variable> evidence, Direction direction) {
        // This means we reach our Goal there for the Variables are not Independent
        if (src.equals(dest)) {
            return false;
        }

        // This means we have reached an evidence variable
        else if (evidence.contains(src)) {

            if (direction != Direction.UP) {

                Iterator<Variable> variableIterator = src.getParents().iterator();
                while (variableIterator.hasNext()) {
                    Variable parent = variableIterator.next();
                    if (!fromParent.get(parent)) {
                        fromParent.put(parent,true);
                        if (!BouncingBall(src, parent, dest, evidence, Direction.UP)) {
                            return false;
                        }
                    }
                }
            }
            return true;


        } else {
            if (direction == Direction.DOWN) {

                Iterator<Variable> variableIterator = src.getChildren().iterator();
                while (variableIterator.hasNext()) {
                    Variable child = variableIterator.next();
                    if (!fromChild.get(child)) {
                        fromChild.put(child, true);
                        if (!BouncingBall(src, child, dest, evidence, Direction.DOWN)) {
                            return false;
                        }
                    }
                }

            } else {

                Iterator<Variable> variableIterator = src.getParents().iterator();
                while (variableIterator.hasNext()) {
                    Variable parent = variableIterator.next();
                    if (!fromParent.get(parent)) {
                        fromParent.put(parent,true);
                        if (parent != from) {
                            if (!BouncingBall(src, parent, dest, evidence, Direction.UP)) {
                                return false;
                            }
                        }
                    }
                }

                variableIterator = src.getChildren().iterator();
                while (variableIterator.hasNext()) {
                    Variable child = variableIterator.next();
                    if (!fromChild.get(child)) {
                        fromChild.put(child, true);
                        if (child != from) {
                            if (!BouncingBall(src, child, dest, evidence, Direction.DOWN)) {
                                return false;
                            }
                        }
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
