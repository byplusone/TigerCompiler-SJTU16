package tiger.RegAlloc;

public class MoveList {
    public tiger.Graph.Node src, dst;
    public MoveList tail;
    public MoveList(tiger.Graph.Node s, tiger.Graph.Node d, MoveList t) {
        src=s; dst=d; tail=t;
    }
}

