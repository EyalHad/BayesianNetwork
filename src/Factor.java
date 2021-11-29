import java.util.*;
import java.util.stream.Collectors;

public class Factor {

    private static int nums;
    public int id;
    List<String> names;
    private String[][] table;


    private static Network net;

    public static void setNet(Network network) {
        net = network;
    }

    public Factor(Variable variable, String[] evidences, int ID) {
        if (ID == 0) {
            Factor.nums = 0;
        } else {
            Factor.nums++;
        }
        this.id = ID;
        this.names = new ArrayList<>();

        int rows = variable.getCpt().getMatrix().length;
        int columns = variable.getCpt().getMatrix()[0].length;
        String[][] temporary = new String[rows][columns];
        for (int i = 0; i < rows; i++)
            temporary[i] = variable.getCpt().getMatrix()[i].clone();

        int minusRows = 0;
        int minusColumns = 0;
        Set<String> evidenceSet = new HashSet<>();
        Set<String> evidenceAndOutcomeSet = new HashSet<>();
        for (String evi : evidences) {
            String[] split = evi.split("=");
            String var = split[0];
            evidenceAndOutcomeSet.add(evi);
            evidenceSet.add(var);
        }

        for (int i = 0; i < temporary[0].length - 1; i++) {
            String varString = temporary[0][i];
            for (String fromSet : evidenceSet) {
                if (varString.equals(fromSet.split("=")[0])) {
                    minusColumns++;
                }
            }
        }
        for (int i = 0; i < temporary[0].length - 1; i++) {

            String varString = temporary[0][i];
            names.add(varString);
            for (int j = 1; j < temporary.length; j++) {
                String variableAndOutcome = varString + "=" + temporary[j][i];
                if (evidenceSet.contains(varString)) {
                    if (!evidenceAndOutcomeSet.contains(variableAndOutcome)) {
                        for (int k = 0; k < temporary[0].length; k++) {
                            temporary[j][k] = "-";
                        }

                    }
                }
            }

        }
        for (int i = 0; i < temporary.length; i++) {
            boolean flag = false;
            for (int j = 0; j < temporary[0].length; j++) {
                if (temporary[i][j].equals("-")) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                minusRows++;
            }
        }

        names.removeAll(evidenceSet);
        if (minusColumns == 0 && minusRows == 0) {
            this.table = temporary;
        } else {

            this.table = new String[rows - minusRows][columns - minusColumns];

            int tableColumn = 0;
            for (int i = 0; i < temporary[0].length; i++) {
                if (!evidenceSet.contains(temporary[0][i])) {
                    int tablesRow = 0;
                    for (int j = 0; j < temporary.length; j++) {
                        if (!temporary[j][i].equals("-")) {
                            this.table[tablesRow++][tableColumn] = temporary[j][i];
                        }
                    }
                    tableColumn++;
                }
            }
        }


    }

    public Factor(Factor left, Factor right) {

        Factor.nums++;
        this.id = Factor.nums;

        List<String> inCommon = new ArrayList<>();
        List<String> unCommon = new ArrayList<>();

        for (String name1 : left.names) {
            if (right.names.contains(name1)) {
                inCommon.add(name1);
            } else {
                unCommon.add(name1);
            }
        }
        for (String name2 : right.names) {
            if (left.names.contains(name2)) {
                inCommon.add(name2);
            } else {
                unCommon.add(name2);
            }
        }

        inCommon = inCommon.stream().distinct().collect(Collectors.toList());
        unCommon = unCommon.stream().distinct().collect(Collectors.toList());

        this.names = new ArrayList<>(inCommon);
        this.names.addAll(unCommon);

        int columns = names.size() + 1;
        int rows = 1;
        for (String name : names) {
            int outcomes = net.getVariable(name).getOutComes().length;
            rows *= outcomes;
        }
        rows += 1;
        this.table = new String[rows][columns];


        int multi = 1;
        for (int i = 0; i < names.size(); i++) {
            this.table[0][i] = names.get(i);
            Variable variable = net.getVariable(this.table[0][i]);
            int loop = multi;
            int row = 1;
            while (row < rows) {
                for (String outcome : variable.getOutComes()) {
                    int index = 0;
                    while (index < loop) {
                        this.table[row][i] = outcome;
                        row++;
                        index++;
                    }
                }
            }
            multi *= variable.getOutComes().length;

        }
        // Just for Readability
        this.table[0][names.size()] = "Probability";

        for (int p = 1; p < this.table.length; p++) {
            List<String> newRow = new ArrayList<>();
            for (int i = 0; i < this.table[0].length - 1; i++) {
                String variableAndOutcome = this.table[0][i] + "=" + this.table[p][i];
                newRow.add(variableAndOutcome);
            }

//            System.out.println("Row to find\n" + newRow);

            for (int rowLeftIndex = 1; rowLeftIndex < left.table.length; rowLeftIndex++) {
                List<String> leftRow = new ArrayList<>();
                for (int i = 0; i < left.table[0].length - 1; i++) {
                    String leftVariableAndOutcome = left.table[0][i] + "=" + left.table[rowLeftIndex][i];
                    leftRow.add(leftVariableAndOutcome);
                }

                if (newRow.containsAll(leftRow)) {

//                    System.out.println("LeftRow found\n" + leftRow);

                    for (int rowRightIndex = 1; rowRightIndex < right.table.length; rowRightIndex++) {
                        List<String> rightRow = new ArrayList<>();
                        for (int i = 0; i < right.table[0].length - 1; i++) {
                            String rightVariableAndOutcome = right.table[0][i] + "=" + right.table[rowRightIndex][i];
                            rightRow.add(rightVariableAndOutcome);
                        }

                        if (newRow.containsAll(rightRow)) {
//                            System.out.println("RightRow found\n" + rightRow);
                            double leftValue = Double.parseDouble(left.table[rowLeftIndex][left.table[0].length - 1]);
                            double rightValue = Double.parseDouble(right.table[rowRightIndex][right.table[0].length - 1]);
                            double result = leftValue * rightValue;
                            this.table[p][this.table[0].length - 1] = result + "";
                            VariableElimination.addMultiply();
                            break;
                        }
                    }
                    break;
                }
            }

        }

    }

    public Factor(Factor toSum, String hidden) {
        Factor.nums++;
        this.id = Factor.nums;
        this.names = new ArrayList<>();
        for (int i = 0; i < toSum.table[0].length - 1; i++) {
            if (!hidden.equals(toSum.table[0][i])) {
                this.names.add(toSum.table[0][i]);
            }
        }


        Variable hiddenVariable = net.getVariable(hidden);
        int rows = toSum.table.length / hiddenVariable.getOutComes().length;
        int columns = toSum.table[0].length - 1;
        this.table = new String[rows + 1][columns];

        int multi = 1;

        for (int i = 0; i < names.size(); i++) {
            this.table[0][i] = names.get(i);
            Variable variable = net.getVariable(this.table[0][i]);
            int loop = multi;
            int row = 1;
            while (row < rows) {
                for (String outcome : variable.getOutComes()) {
                    int index = 0;
                    while (index < loop) {
                        this.table[row][i] = outcome;
                        row++;
                        index++;
                    }
                }
            }
            multi *= variable.getOutComes().length;

        }
        this.table[0][names.size()] = "Probability";


        for (int p = 1; p < this.table.length; p++) {
            List<String> newRow = new ArrayList<>();
            for (int i = 0; i < this.table[0].length - 1; i++) {
                String variableAndOutcome = this.table[0][i] + "=" + this.table[p][i];
                newRow.add(variableAndOutcome);
            }

//            System.out.println("Row to find\n" + newRow);

            for (int rowLeftIndex = 1; rowLeftIndex < toSum.table.length; rowLeftIndex++) {
                List<String> toSumRow1 = new ArrayList<>();
                String compare = "";
                for (int i = 0; i < toSum.table[0].length - 1; i++) {
                    if (hidden.equals(toSum.table[0][i])) {
                        compare = toSum.table[0][i] + "=" + toSum.table[rowLeftIndex][i];
                    } else {
                        String leftVariableAndOutcome = toSum.table[0][i] + "=" + toSum.table[rowLeftIndex][i];
                        toSumRow1.add(leftVariableAndOutcome);
                    }

                }

                if (toSumRow1.containsAll(newRow)) {

//                    System.out.println("LeftRow found\n" + toSumRow1);

                    for (int rowRightIndex = 1; rowRightIndex < toSum.table.length; rowRightIndex++) {
                        String withCompare = "";
                        List<String> toSumRow2 = new ArrayList<>();
                        for (int i = 0; i < toSum.table[0].length - 1; i++) {
                            if (hidden.equals(toSum.table[0][i])) {
                                withCompare = toSum.table[0][i] + "=" + toSum.table[rowRightIndex][i];
                            } else {
                                String rightVariableAndOutcome = toSum.table[0][i] + "=" + toSum.table[rowRightIndex][i];
                                toSumRow2.add(rightVariableAndOutcome);
                            }

                        }


                        if (toSumRow2.containsAll(newRow) && !compare.equals(withCompare)) {
//                            System.out.println("RightRow found\n" + toSumRow2);
                            double leftValue = Double.parseDouble(toSum.table[rowLeftIndex][toSum.table[0].length - 1]);
                            double rightValue = Double.parseDouble(toSum.table[rowRightIndex][toSum.table[0].length - 1]);
//                            System.out.println("LEFT" + leftValue);
//                            System.out.println("Right" + rightValue);
                            double result = leftValue + rightValue;
//                            System.out.println("RESULT" + result);
                            this.table[p][this.table[0].length - 1] = result + "";
                            VariableElimination.addAddition();
                            break;
                        }
                    }
                    break;
                }
            }

        }
    }

    public static double Normalize(Factor toNormal) {
        double sum = 0;
        for (int i = 1; i < toNormal.table.length; i++) {
            double add =
                    Double.parseDouble(
                            toNormal.table[i][toNormal.table[i].length - 1]
                    );
            sum += add;
            VariableElimination.addAddition();
        }
        return sum;
    }

    public static double getQueryValue(Factor factorQ, String query) {
        double value = 0;
        for (int i = 1; i < factorQ.table.length; i++) {

            String compare = factorQ.table[0][0] + "=" + factorQ.table[i][0];
            if (query.equals(compare)) {
                value = Double.parseDouble(factorQ.table[i][factorQ.table[i].length - 1]);
            }
        }
        return value;
    }

    public int getTableSize() {
        return table.length * table[0].length;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(names.toString()).append("\n");
        for (int i = 1; i < this.table.length; i++) {
            for (int j = 0; j < this.table[i].length - 1; j++) {
                stringBuilder.append("[").append(this.table[i][j]).append("],");
            }
            stringBuilder.append(" => ");
            double Dprint = Double.parseDouble(this.table[i][this.table[i].length - 1]);
            String print = String.format("%.5f", Dprint);
            stringBuilder.append(print);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
