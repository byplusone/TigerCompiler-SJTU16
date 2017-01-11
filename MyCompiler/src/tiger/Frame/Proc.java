package tiger.Frame;

import tiger.Assem.InstrList;

public class Proc {
    public String a, b;

    public InstrList body;

    public Proc(String name, InstrList bd, String t) {
        a = name;
        body = bd;
        b = t;
    }
}
