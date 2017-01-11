package tiger.Graph;

import java.util.*;

public class GraphNodeInfo {
    public Set<tiger.Temp.Temp> in = new HashSet<tiger.Temp.Temp>(); //来指令前的活性变量 
    public Set<tiger.Temp.Temp> out = new HashSet<tiger.Temp.Temp>(); //出指令后的活性变量 ,即活跃变量
    public Set<tiger.Temp.Temp> use = new HashSet<tiger.Temp.Temp>(); //某指令使用的变量 ,赋值号右边
    public Set<tiger.Temp.Temp> def = new HashSet<tiger.Temp.Temp>(); //某指令定义的变量 ,赋值号左边 

    public GraphNodeInfo(Node node)
    {
        for (tiger.Temp.TempList i = ((tiger.FlowGraph.FlowGraph)node.mygraph).def(node); i != null; i = i.tail)
        {
            def.add(i.head);
        }
        for (tiger.Temp.TempList i = ((tiger.FlowGraph.FlowGraph)node.mygraph).use(node); i != null; i = i.tail)
        {
            use.add(i.head);
        }
    }
}