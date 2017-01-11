package tiger.RegAlloc;

public class RegAlloc implements tiger.Temp.TempMap{
    private tiger.Assem.InstrList instrs;
    private Color color;

    public String tempMap(tiger.Temp.Temp t)
    {
        return color.tempMap(t);
    }
    public RegAlloc(tiger.Frame.Frame f, tiger.Assem.InstrList instrs)
    {
        this.instrs = instrs;
        tiger.FlowGraph.FlowGraph flowGraph = new tiger.FlowGraph.AssemFlowGraph(instrs); //根据汇编指令生成流图
        InterferenceGraph interGraph=new Liveness(flowGraph); //进行活性分析并生成冲突图
        color = new Color(interGraph, f, f.registers()); //用着色法分配寄存器
    }
}