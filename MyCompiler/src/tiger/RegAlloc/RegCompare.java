package tiger.RegAlloc;

import java.util.Comparator;
import tiger.Temp.Temp;


public class RegCompare implements Comparator<Temp> {
    public int compare(Temp t1, Temp t2){
        if(t1.num>t2.num) return 1;
        else if(t1.num<t2.num) return -1;
        else return 0;
    }


}
