package tiger.Frame;

import tiger.Util.*;
import tiger.Tree.Expr;

public class AccessList{
    public Access head;
    public AccessList tail;

    public AccessList(Access head, AccessList tail){
        this.head = head;
        this.tail = tail;
    }
}