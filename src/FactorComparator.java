import java.util.Comparator;
import java.util.List;

public class FactorComparator implements Comparator<Factor> {


    @Override
    public int compare(Factor a, Factor b) {
        if (a.factorTable.size() > b.factorTable.size()) return 1;
        else if (a.factorTable.size() < b.factorTable.size()) return -1;
        return Integer.compare(stringASCII(a.names),stringASCII(b.names));
    }

    private int stringASCII(List<String> strings) {
        int sum = 0;
        for (String s :
                strings) {
            for (int i = 0; i < s.length(); i++) {
                sum += s.charAt(i);
            }
        }
        return sum;
    }

}
