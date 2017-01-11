package tiger.RegAlloc;

import tiger.Graph.GraphNodeInfo;
import tiger.Graph.Node;

import java.util.*;

public class Liveness extends InterferenceGraph{
    private tiger.FlowGraph.FlowGraph flowGraph = null;
    private Hashtable<tiger.Graph.Node, tiger.Graph.GraphNodeInfo> node2nodeInfo = new Hashtable<tiger.Graph.Node, tiger.Graph.GraphNodeInfo>();
    //用一个哈希表记录每个点的use\def和in\out迭代信息
    private Hashtable<tiger.Graph.Node, tiger.Temp.TempList> liveMap = new Hashtable<tiger.Graph.Node, tiger.Temp.TempList>();
    //用一个哈希表记录每个节点和其对应的活跃寄存器
    private Hashtable<tiger.Temp.Temp, tiger.Graph.Node> temp2node = new Hashtable<tiger.Temp.Temp, tiger.Graph.Node>();
    private Hashtable<tiger.Graph.Node, tiger.Temp.Temp> node2temp = new Hashtable<tiger.Graph.Node, tiger.Temp.Temp>();
    //用两个哈希表实现由寄存器查找对应的节点(指令),以及由节点(指令)查找对应的寄存器
    //这里其实有一个更简单的实现,就是运用hashmap,hashmap可实现双向查找,即一对元素同时是key和value

    public MoveList moves()
    {
        return null;
    }
    public Liveness(tiger.FlowGraph.FlowGraph f)
    {
        this.flowGraph = f;
        initNodeInfo();
        //初始化
        calcLiveness();
        //活性分析
        buildGraph();
        //生成冲突图
    }
    private void initNodeInfo()
    {
        //初始化
        for(tiger.Graph.NodeList nodes = flowGraph.nodes(); nodes != null; nodes = nodes.tail)
        {
            //将每一个点的def与use输入到哈希表中
            GraphNodeInfo ni = new tiger.Graph.GraphNodeInfo(nodes.head);
            node2nodeInfo.put(nodes.head, ni);
        }
    }
    private void calcLiveness()
    {
        //计算活性 live-in 和 live-out
        //数据流等式:
        // in[n]=use[n] U (out[n]-def[n])
        //out[n]=U (in[s]) , for each s in succ[n]
        //反复迭代,直到不变
        boolean flag = false;
        while (!flag)
        {
            flag = true;//先假设不变
            for(tiger.Graph.NodeList nodes = flowGraph.nodes(); nodes != null; nodes = nodes.tail)
            {
                GraphNodeInfo ni = node2nodeInfo.get(nodes.head);
                //查找哈希表
                Set<tiger.Temp.Temp> inx = new HashSet<tiger.Temp.Temp>();
                inx.addAll(ni.out);
                inx.removeAll(ni.def);
                inx.addAll(ni.use);
                // in[n]=use[n] U (out[n]-def[n])
                if (!inx.equals(ni.in)) flag = false;
                //若与之前的迭代结果不同则需要继续迭代
                node2nodeInfo.get(nodes.head).in = inx;
                //更新迭代值
                Set<tiger.Temp.Temp> outx = new HashSet<tiger.Temp.Temp>();
                for (tiger.Graph.NodeList succ = nodes.head.succ(); succ != null; succ = succ.tail)
                {
                    GraphNodeInfo i = (GraphNodeInfo) node2nodeInfo.get(succ.head);
                    outx.addAll(i.in);
                }
                //out[n]=U (in[s])
                if (!outx.equals(ni.out)) flag = false;
                //若与之前的迭代结果不同则需要继续迭代
                node2nodeInfo.get(nodes.head).out = outx;
                //更新迭代值
            }
        }

        for (tiger.Graph.NodeList nodes = flowGraph.nodes(); nodes != null; nodes = nodes.tail)
        {
            //生成livemap
            tiger.Temp.TempList tl = null;
            //得到活性信息中活跃变量的迭代器
            for (Iterator i = ((GraphNodeInfo)node2nodeInfo.get(nodes.head)).out.iterator(); i.hasNext(); )
                tl = new tiger.Temp.TempList((tiger.Temp.Temp) i.next(), tl);
            if (tl != null)	liveMap.put(nodes.head, tl);
            //livemap中,将每个节点与在这个节点中的活跃变量关联起来
        }
    }
    private void buildGraph()
    {
        Set temps = new HashSet();
        for (tiger.Graph.NodeList node = flowGraph.nodes(); node != null; node = node.tail)
        {
            //加入流图中所有的有关变量,包括定值的和引用的
            for (tiger.Temp.TempList t = flowGraph.use(node.head); t != null; t = t.tail)
                temps.add(t.head);
            for (tiger.Temp.TempList t = flowGraph.def(node.head); t != null; t = t.tail)
                temps.add(t.head);
        }

        Iterator i = temps.iterator();
        while (i.hasNext()) add(newNode(), (tiger.Temp.Temp) i.next());
        //生成原始的冲突图，此时每个节点代表一个变量
        for(tiger.Graph.NodeList nodes = flowGraph.nodes(); nodes != null; nodes = nodes.tail)
            //遍历流图中的每一条指令
            for (tiger.Temp.TempList t = flowGraph.def(nodes.head); t != null; t = t.tail)
                //遍历每一条指令中被定值的变量
                for (tiger.Temp.TempList t1 = (tiger.Temp.TempList) liveMap.get(nodes.head); t1 != null; t1 = t1.tail)
                {
                    //找到每条指令中的出口活跃变量
                    //加边时首先判断是否t和t1相等,若是同一个变量那么加冲突边是没有意义的,若加了会出现回路
                    if (t.head != t1.head && !flowGraph.isMove(nodes.head))
                    {
                        //加边规则1:若非传送指令,那么在定值变量t和所有出口活跃变量t1之间加边,注意加边是双向的
                        addEdge(tnode(t.head), tnode(t1.head));
                        addEdge(tnode(t1.head), tnode(t.head));
                    }
                    if (t.head != t1.head && flowGraph.isMove(nodes.head) && flowGraph.use(nodes.head).head != t1.head)
                    {
                        //加边规则2:若为传送指令move t,t0 ,t0由这条指令的use给出,那么在定值变量t和所有除了t0以外的出口活跃变量t1之间加边
                        addEdge(tnode(t.head), tnode(t1.head));
                        addEdge(tnode(t1.head), tnode(t.head));
                    }
                    //其实此处有更简单的写法,即只需除去同时满足ismove且引用变量出口活跃的情况即可,如下所示,只需一个if语句：
                    //if (t.head != t1.head && !(flowGraph.isMove(node.head) && flowGraph.use(node.head).head == t1.head))
                }
    }
    void add(tiger.Graph.Node node, tiger.Temp.Temp temp)
    {
        //在冲突图中加边
        temp2node.put(temp, node);
        node2temp.put(node, temp);
    }
    public Node tnode(tiger.Temp.Temp temp)
    {
        //由变量查找相对应的节点
        return temp2node.get(temp);
    }
    public tiger.Temp.Temp gtemp(Node node)
    {
        //由节点查找相对应的变量
        return node2temp.get(node);
    }
}
