//package tiger.Frame;
//
//import tiger.Temp.*;
//import tiger.Tree.*;
//import tiger.Util.BoolList;
//import tiger.Assem.*;
//
//public abstract class Frame implements TempMap {
//    public Label name;
//    public AccessList formals;
//    public static final Label error = new Label("error");
//    public static Label getError() {
//        return error;
//    }
//    abstract public Frame newFrame(Label name, BoolList formals);
//    abstract public Access allocLocal(boolean escape);
//    abstract public Access allocArguLocal(boolean escape);
//    abstract public int Offset();
//    abstract public Temp FP();
//    abstract public Temp SP();
//    abstract public Temp RV();
//    abstract public Temp RA();
//    abstract public Temp ARGU(int n);
//    abstract public TempList freeRegs();
//    abstract public TempList colors();
//    abstract public TempList registers();
//    abstract public String tempMap(Temp temp);
//    abstract public int wordSize();
//    abstract public Expr externalCall(String func, ExpList args);
//    abstract public String string(Label lab, String lit);
//    abstract public Stm procEntryExit1(Stm body);
//    abstract public InstrList procEntryExit2(InstrList body);
//    abstract public InstrList procEntryExit3(InstrList body);
//    abstract public InstrList codegen(Stm stm);
//    // abstract public Temp newTemp();
//
//    static public InstrList append(InstrList a, InstrList b) {
//        if (a == null)
//            return b;
//        if (b == null)
//            return a;
//        InstrList p;
//        for (p = a; p.tail != null; p = p.tail)
//            ;
//        p.tail = b;
//        return a;
//    }
//
//    static public TempList L(Temp h, TempList t) {
//        return new TempList(h, t);
//    }
//
//    static public TempList L(Temp h) {
//        return new TempList(h, null);
//    }
//
//    // abstract public static String programTail();
//}

package tiger.Frame;

import java.util.LinkedList;
import tiger.Temp.*;
import tiger.Tree.*;
import tiger.Util.*;
import tiger.Assem.InstrList;

public abstract class Frame implements TempMap {
    //建立新帧(名称、参数逃逸信息)
    public abstract Frame newFrame(Label name, BoolList formals);

    public Label name; //名称
    public AccessList formals = null; //本地变量(局部量、参数)列表
    public abstract Access allocLocal(boolean escape); //分配新本地变量(是否逃逸)
    public abstract Expr externalCall(String func, ExpList args); //外部函数
    public abstract Temp FP(); //帧指针
    public abstract Temp SP(); //栈指针
    public abstract Temp RA(); //返回地址
    public abstract Temp RV(); //返回值
    //public abstract Temp ARGU(int n);
    public abstract java.util.HashSet registers(); //寄存器列表
    //public abstract TempList registers(); //寄存器列表
    public abstract Stm procEntryExit1(Stm body); //添加额外函数调用指令,见 5.4
    public abstract InstrList procEntryExit2(InstrList body); //同上
    public abstract InstrList procEntryExit3(InstrList body); //同上
    public abstract String string(Label label, String value);
    public abstract InstrList codegen(Stm s); //生成 MIPS 指令用
    public abstract int wordSize(); //返回一个字长(定义为 4bytes)
    public abstract TempList colors();

    static public TempList L(Temp h, TempList t) {
        return new TempList(h, t);
    }

    static public TempList L(Temp h) {
        return new TempList(h, null);
    }
}