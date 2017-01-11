package tiger.RegAlloc;
import tiger.Graph.Node;
import tiger.Graph.Graph;

abstract public class InterferenceGraph extends Graph {
   abstract public Node tnode(tiger.Temp.Temp temp);
   abstract public tiger.Temp.Temp gtemp(Node node);
   abstract public MoveList moves();
   public int spillCost(Node node) {return 1;}
}
