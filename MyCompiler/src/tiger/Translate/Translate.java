package tiger.Translate;
import tiger.Temp.Temp;
import tiger.Temp.Label;
import tiger.Tree.*;

import java.util.ArrayList;

public class Translate {
  public tiger.Frame.Frame frame;
  private int wordSize = 4;
  private Frag frags = null;

  public Translate(tiger.Frame.Frame f) {
    frame = f;
  }
  
  public Frag getResult() {
    return frags;
  }
  
  public void addFrag(Frag frag) {
    frag.next = frags;
    frags = frag;
  }
  
  public void procEntryExit(Level level, Exp body, boolean returnValue) {
	  Stm b = null;
	  if (returnValue)
	  //若有返回值，将返回值存入$v0
	  b = new MOVE(new TEMP(level.frame.RV()), body.unEx());
	  else
	  //若无返回值，则产生Nx节点。
	  b = body.unNx();
	  b = level.frame.procEntryExit1(b);
	  //添加一个新的程序段，
	  //程序段的含义在后边说明
	  addFrag(new ProcFrag(b, level.frame)); 
	  }
  
  public Exp transNilExp() {
	  //产生一个空语句
    return new Ex(new CONST(0));
  }
  
  public Exp transIntExp(int value) {
	  //将整型数翻译成CONST节点
    return new Ex(new CONST(value));
  }
  
  public Exp transStringExp(String value) {
	  //字符串将产生一个新的数据段DataFrag
    Label l = new Label(); 
    addFrag(new DataFrag(l, frame.string(l, value)));
    return new Ex(new NAME(l));
  }

  private static tiger.Tree.Expr CONST(int value) {
	  //常数依旧翻译成为常数
	    return new tiger.Tree.CONST(value);
	  }
	  
  public Exp Error() {
	    return new Ex(CONST(0));
	  }
	
  public Exp transArrayExp(Level home, Exp init, Exp size) {
	//调用外部函数 initArray 为数组在frame 上分配存储空间,并得到
	//存储空间首地址
	//initArray 执行如下的类C 代码,需要提供数组大小与初始值
	//# int *initArray(int size, int init)
	//# {int i;
	//# int *a = (int *)malloc(size*sizeof(int));
      tiger.Tree.Expr alloc = home.frame.externalCall("initArray", new tiger.Tree.ExpList(size.unEx(), new tiger.Tree.ExpList(init.unEx(), null)));
    return new Ex(alloc);
  }
  
  public Exp transNoOp() {
	  //同Nil一样，产生空语句
    return new Ex(new CONST(0));
  }
  
  public Exp transStringRelExp(int oper, Exp left, Exp right) {
	  //标准库中提供了字符串比较的库，所以只需要调用，然后进行判断即可
      tiger.Tree.Expr comp = frame.externalCall("_stringCompare", new tiger.Tree.ExpList(left.unEx(),
            new tiger.Tree.ExpList(right.unEx(), null)));
    return new RelCx(oper, new Ex(comp), new Ex(new CONST(0)));
  }
  
  public Exp transStringRelExp(Level currentL, int oper, Exp left, Exp right)
	{
	//标准库中提供了字符串比较的库，所以只需要调用，然后进行判断即可
        tiger.Tree.Expr comp = currentL.frame.externalCall("stringEqual", new tiger.Tree.ExpList(left.unEx(), new tiger.Tree.ExpList(right.unEx(), null)));
		return new RelCx(oper, new Ex(comp), new Ex( new CONST(1)));
	}
  
  public Exp transCalcExp(int binOp, Exp left, Exp right) {
	  //算数运算产生BINOP节点
    return new Ex(new BINOP(binOp, left.unEx(), right.unEx()));
  }
  
  public Exp transOtherRelExp(int oper, Exp left, Exp right) {
    return new RelCx(oper, left, right);
  }
  
  public Exp transAssignExp(Exp lvalue, Exp ex) {
	  //赋值语句产生MOVE节点
    return new Nx(new MOVE(lvalue.unEx(), ex.unEx()));
  }

  public Exp transCallExp(Level home, Level dest, Label name, ArrayList<Exp> argValue) {
	//抽取参数
      tiger.Tree.ExpList args = null;
    for (int i = argValue.size() - 1; i >= 0; --i)
      args = new tiger.Tree.ExpList(((Exp) argValue.get(i)).unEx(), args);
    Level l = home;
      tiger.Tree.Expr slnk = new TEMP(l.frame.FP()); //静态链接
  //找到Callee 直接上层的静态连接
    while (dest.parent != l) {
      slnk = l.staticLink().acc.exp(slnk);
      l = l.parent;
    }
    args = new tiger.Tree.ExpList(slnk, args);
    return new Ex(new CALL(new NAME(name), args));
  }

  public Exp transVarExp(Exp ex) {
    return ex;
  }
  
  public Exp transOpExp(int oper, Exp left, Exp right)
	{	
		if (oper >= BINOP.PLUS && oper <= BINOP.DIV)
			return new Ex(new BINOP(oper, left.unEx(), right.unEx()));
		return new RelCx(oper, left, right);
	}

  public Exp transIfThenElseExp(Exp test, Exp e_then, Exp e_else) {
    return new IfExp(test, e_then, e_else);
  }

  public Exp combine2Stm(Exp e1, Exp e2) {
    if (e1 == null)
      return new Nx(e2.unNx());
    else if (e2 == null)
      return new Nx(e1.unNx());
    else
      return new Nx(new SEQ(e1.unNx(), e2.unNx()));
  }

  public Exp combine2Exp(Exp e1, Exp e2) {
    if (e1 == null)
      return new Ex(e2.unEx());
    else
      return new Ex(new ESEQ(e1.unNx(), e2.unEx()));
  }

  public Exp transRecordExp(Level home, ArrayList<Exp> field) {
    Temp addr = new Temp();
  //调用外部函数 _allocRecord 为记录在frame 上分配空间,
 // 并得存储空间首地址
 //_allocRecord 执行如下的类C 代码,注意它只负责分配空间
 //初始化操作需要我们来完成
 //# int *allocRecord(int size)
 //# {int i;
 //# int *p, *a;
 //# p = a = (int *)malloc(size);
 //# for(i=0;i<size;i+=sizeof(int)) *p++ = 0;
 //# return a;
 //# }
 //注意如果记录为空,也要用1 个word,否则每个域为一个word,按顺序存放
      tiger.Tree.Expr alloc = home.frame.externalCall("_allocRecord",
            new tiger.Tree.ExpList(new CONST((
                    field.size() == 0 ? 1 : field.size())
                    * home.frame.wordSize()), null));
    Stm init = new EXP(new CONST(0));
    for (int i = field.size() - 1; i >= 0; i--) {
    	//为记录中每个域生成MOVE 指令,将值复制到帧中的相应区域
        tiger.Tree.Expr offset = new BINOP(BINOP.PLUS,
              new TEMP(addr), new CONST(i * home.frame.wordSize()));
        tiger.Tree.Expr v = field.get(i).unEx();
    	init = new SEQ(new MOVE(new MEM(offset), v), init);
    }
  //返回记录的首地址
    return new Ex(new ESEQ(new SEQ(new MOVE(new TEMP(addr), alloc), init), new TEMP(addr)));
  }

  public Exp transWhileExp(Exp test, Exp body, Label done) {
    return new WhileExp(test, body, done);
  }

  public Exp transForExp(Level home, Access var, Exp low, Exp high, Exp body, Label done) {
    return new ForExp(home, var, low, high, body, done);
  }

  public Exp transSimpleVar(Access access, Level home) {
      tiger.Tree.Expr res = new TEMP(home.frame.FP());
    Level l = home;
    while (l != access.home) {
      res = l.staticLink().acc.exp(res);
      l = l.parent;
    }
    return new Ex(access.acc.exp(res));
    //return new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, framePtr, new Tree.CONST(offset)));
 }

  public Exp transSubscriptVar(Exp var, Exp idx) {
      tiger.Tree.Expr arr_addr = var.unEx(); //arr_addr数组首地址
	//arr_off 偏移量, 等于下标乘以字长
      tiger.Tree.Expr arr_off = new BINOP(BINOP.MUL, idx.unEx(), new CONST(wordSize));
    return new Ex(new MEM(new BINOP(BINOP.PLUS, arr_addr, arr_off)));
  }

  public Exp transFieldVar(Exp var, int num) {
      tiger.Tree.Expr rec_addr = var.unEx(); //记录首地址
	//偏移量 (每个记录项目占一个wordsize)
      tiger.Tree.Expr rec_off = new CONST(num * wordSize);
    return new Ex(new MEM(new BINOP(BINOP.PLUS, rec_addr, rec_off)));
  }

  /*
  public EXP transLetExp(ExpList eDec, EXP body, boolean isVOID) {
    if (isVOID)
      return new Nx(new SEQ(transSeqExp(eDec, true).unNx(), body.unNx()));
    else
      return new Ex(new ESEQ(transSeqExp(eDec, true).unNx(), body.unEx()));
  }
  */
  
  /*Exp transBreakExp(){
  return new Nx(new JUMP((Label) loopExit.peek()));
  }*/
  
  public Exp transStdCallExp(Level currentL, Label name, java.util.ArrayList<Exp> args_value)
  {
      tiger.Tree.ExpList args = null;
      for (int i = args_value.size() - 1; i >= 0; --i)
          args = new tiger.Tree.ExpList(((Exp) args_value.get(i)).unEx(), args);
      return new Ex(currentL.frame.externalCall(name.toString(), args));
  }

  public Exp transExtCallExp(Level home, Label name, ArrayList argValue) {
      tiger.Tree.ExpList args = null;
	  for (int i = argValue.size() - 1; i >= 0; --i)
		  args = new tiger.Tree.ExpList(((Exp) argValue.get(i)).unEx(), args);
	  Level l = home;
      tiger.Tree.Expr slnk = new TEMP(l.frame.FP());
	  args = new tiger.Tree.ExpList(slnk, args);
	  return new Ex(home.frame.externalCall("_" + name, args));
  }
		  
  public Exp transBreakExp(Label done) {
    return new Nx(new JUMP(done));
  }

  public Exp transMultiArrayExp(Level home, Exp init, Exp size) {
      tiger.Tree.Expr alloc = home.frame.externalCall("_malloc",
            new tiger.Tree.ExpList(
                    new BINOP(BINOP.MUL, size.unEx(), new CONST(frame.wordSize())),
                    null));
    Temp addr = new Temp();
    Access var = home.allocLocal(false);
    Stm initialization = (new ForExp(home, var, new Ex(new CONST(0)),
            new Ex(new BINOP(BINOP.MINUS, size.unEx(), new CONST(1))),
            new Nx(new MOVE(
                    new MEM(new BINOP(BINOP.PLUS, new TEMP(addr), new BINOP(BINOP.MUL, var.acc.exp(null), new CONST(frame.wordSize())))),
                    init.unEx())),
            new Label())).unNx();
    return new Ex(new ESEQ(new SEQ(new MOVE(new TEMP(addr), alloc), initialization), new TEMP(addr)));
  }

  public Exp transSeqExp(ExpList el, boolean isVOID) {
    if (el == null) return new Ex(new CONST(0));
    if (el.tail == null) return el.head;
    if (el.tail.tail == null)
      if (isVOID)
        return new Nx(new SEQ(el.head.unNx(), el.tail.head.unNx()));
      else
        return new Ex(new ESEQ(el.head.unNx(), el.tail.head.unEx()));
    ExpList ptr = el.tail, prev = el;
    SEQ res = null;
    for (; ptr.tail != null; ptr = ptr.tail) {
      if (res == null)
        res = new SEQ(prev.head.unNx(), ptr.head.unNx());
      else
        res = new SEQ(res, ptr.head.unNx());
    }
    if (isVOID)
      return new Nx(new SEQ(res, ptr.head.unNx()));
    else
      return new Ex(new ESEQ(res, ptr.head.unEx()));
  }

}