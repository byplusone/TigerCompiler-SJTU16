package tiger.Mips;

import tiger.Temp.Temp;

class InReg extends tiger.Frame.Access {
  Temp temp;
  public InReg() {
    temp = new Temp();
  }

  public tiger.Tree.Expr exp(tiger.Tree.Expr fp) {
    return new tiger.Tree.TEMP(temp);
  }
  
  public tiger.Tree.Expr expFromStack(tiger.Tree.Expr stackPtr) {
	  return new tiger.Tree.TEMP(temp);
  }

  /*public String toString() {
    return temp.toString();
  }*/
}
