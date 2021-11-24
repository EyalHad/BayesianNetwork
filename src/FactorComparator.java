import java.util.Comparator;

public class FactorComparator implements Comparator<Factor> {


    @Override
    public int compare(Factor a, Factor b) {
        if ( a.factorTable.size() < b.factorTable.size() ) return 1;
        else if (a.factorTable.size() > b.factorTable.size()) return -1;
        return Integer.compare(stringASCII(a.name),stringASCII(b.name));
    }

    private int stringASCII(String s){
        int sum = 0;
        for (int i = 0; i < s.length(); i++) {
            sum += s.charAt(i);
        }
        return sum;
    }

}
