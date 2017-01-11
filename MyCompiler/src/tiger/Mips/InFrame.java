package tiger.Mips;

class InFrame extends tiger.Frame.Access {
  private MipsFrame frame;
  int offset;
  InFrame(MipsFrame frame,int o) {
	this.frame = frame;
	this.offset = o;
  }

  public tiger.Tree.Expr exp(tiger.Tree.Expr fp) {
    return new tiger.Tree.MEM
      (new tiger.Tree.BINOP(tiger.Tree.BINOP.PLUS, fp, new tiger.Tree.CONST(offset)));
  }
  
  public tiger.Tree.Expr expFromStack(tiger.Tree.Expr stackPtr) {
	  return new tiger.Tree.MEM(new tiger.Tree.BINOP(tiger.Tree.BINOP.PLUS, stackPtr, new tiger.Tree.CONST(offset
	  - frame.offset - frame.wordSize())));
	  }

  /*public String toString() {
    Integer offset = new Integer(this.offset);
    return offset.toString();
  }*/
}
